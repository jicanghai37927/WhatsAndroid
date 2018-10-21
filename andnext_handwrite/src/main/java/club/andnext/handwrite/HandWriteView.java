package club.andnext.handwrite;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import hanzilookup.HanziLookup;
import hanzilookup.data.CharacterTypeRepository;
import hanzilookup.data.MatcherThread.ResultsHandler;
import hanzilookup.data.MemoryStrokesStreamProvider;
import hanzilookup.data.StrokesDataSource;
import hanzilookup.data.StrokesMatcher;

/**
 *
 */
public class HandWriteView extends View implements ResultsHandler {

	private static final float TOUCH_TOLERANCE = 4;
	
	static final float MINP = 0.25f;
	static final float MAXP = 0.75f;

	float mStrokeWidth;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;

	private Paint mPaint;
	private MaskFilter mEmboss;

	private float mX, mY;

	private HanziLookup mHanziLookup;
	
	private OnHandWriteListener mOnHandWriteListener; 
	private StrokesMatcher mMatcher;
	private Character[] mResult; 
	private int mStrokeCount; 
	
	private int mLastMotionX;		// 最近一次移动的X坐标
	private int mLastMotionY;		// 最近一次移动的Y坐标
	private long mLastMotionTime; 	// 上一次移动时间（毫秒）

    Handler myHandler;

    /**
	 * @param context
	 */
	public HandWriteView(Context context) {
		this(context, null);

	}

	/**
	 * @param context
	 * @param attrs
	 */
	public HandWriteView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public HandWriteView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

        {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HandWriteView);
            if (a != null) {

                final int N = a.getIndexCount();
                for (int i = 0; i < N; i++) {
                    int attr = a.getIndex(i);

                    if (attr == R.styleable.HandWriteView_strokeWidth) {
                        this.mStrokeWidth = a.getDimension(attr, dp2px(context, 6));
                    }

                }

                a.recycle();
            }
        }

        {
            init();
        }
	}

	/**
	 * 
	 */
	private void init() {
        myHandler = new HandWriteHandler(this);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xff0e0e0c);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(mStrokeWidth);
        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                       0.4f, 6, 3.5f);
        
        mPaint.setMaskFilter(mEmboss);
        
        mPaint.setDither(true);
        
		// 创建Lookup
		this.ensureLookup();
	}

	public void setStrokeWidth(float value) {
	    this.mStrokeWidth = value;

	    mPaint.setStrokeWidth(value);
    }

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		// 创建Lookup
		this.ensureLookup();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		
		if (mHanziLookup != null) {
			mHanziLookup.stop();
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		this.clear();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!this.isEnabled()) {
			return false; 
		}
		
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			// 音效
//			App.sfx().play(Sfx.handWriteDown);
//			App.sfx().play(Sfx.handWriteMove);
			
			mHanziLookup.inputCanvas.mousePressed(event);
			touch_start(x, y);
			invalidate();
			
			if (mOnHandWriteListener != null) {
				mOnHandWriteListener.onStrokeBegan(this); 
			}
			
			// 记录参数信息
			this.mLastMotionX = (int)x; 
			this.mLastMotionY = (int)y; 
			this.mLastMotionTime = System.currentTimeMillis(); 
			
			break;
		case MotionEvent.ACTION_MOVE:
			mHanziLookup.inputCanvas.mouseDragged(event);
			touch_move(x, y);
			invalidate();
			
			// 移动
			this.hw_move(x, y, System.currentTimeMillis());
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			
			++mStrokeCount; 
			
			mHanziLookup.inputCanvas.mouseReleased(event);
			touch_up();
			invalidate();
			
			if (mOnHandWriteListener != null) {
				mOnHandWriteListener.onStrokeEnd(this); 
			}
			
			this.performClick(); 
			break;
		}
		
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath(mPath, mPaint);
	}

	@Override
	public void handleResults(StrokesMatcher matcher, Character[] results) {
		this.mMatcher = matcher; 
		this.mResult = results; 
		myHandler.sendEmptyMessage(0); 
	}

	/**
	 * 
	 */
	void ensureLookup() {
		if (mHanziLookup != null) {
			return; 
		}
		
		try {
			byte[] data = readAssetToByteArray(this.getContext(), "strokes.dat");
			MemoryStrokesStreamProvider provider = new MemoryStrokesStreamProvider(data);
			StrokesDataSource source = new StrokesDataSource(provider);
			
			mHanziLookup = new HanziLookup(source);
			mHanziLookup.setSearchType(CharacterTypeRepository.SIMPLIFIED_TYPE);
			mHanziLookup.setResultHandler(this);
			mHanziLookup.setNumResults(18); // 固定为18

		} catch (IOException e) {
			mHanziLookup = null; 
		} 
	}
	
	/**
	 * @return
	 */
	public HanziLookup getHanziLookup() {
		return this.mHanziLookup; 
	}
	
	/**
	 * @param listener
	 */
	public void setOnHandWriteListener(OnHandWriteListener listener) {
		this.mOnHandWriteListener = listener; 
	}
	
	/**
	 * @return
	 */
	public int getStrokeCount() {
		return this.mStrokeCount; 
	}
	
	/**
	 * 清除内容
	 * 
	 */
	public void clear() {
		this.mStrokeCount = 0; 
		
		if (mHanziLookup != null) {
			mHanziLookup.inputCanvas.clear(); 
		}
		
		if (mCanvas != null) {
			
			mBitmap.eraseColor(Color.TRANSPARENT);
			invalidate();
		}
	}

	/**
	 * @param x
	 * @param y
	 */
	private void touch_start(float x, float y) {
		mPath.reset();
		
		mPath.moveTo(x, y);
		
		mX = x;
		mY = y;
	}

	/**
	 * @param x
	 * @param y
	 */
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);

		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	/**
	 * 
	 */
	private void touch_up() {
		mPath.lineTo(mX, mY);

		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);

		// kill this so we don't double draw
		mPath.reset();
		
		
	}

	private void hw_move(float aX, float aY, long aTime) {

		boolean enable = false; 
		if (!enable) {
			return; 
		}
		
		long ellapse = (aTime - mLastMotionTime); 
		if (ellapse <= 0) {
			return; 
		}
		
		int x = (int)aX; 
		int y = (int)aY; 
		
		int deltaX = (x - mLastMotionX); 
		int deltaY = (y - mLastMotionY); 
		int distance = deltaX * deltaX + deltaY * deltaY; 
		
		double speed = Math.sqrt(distance); 
		speed = speed / ellapse;
		
		float pitch = (float)(speed); 
		if (pitch >= 0.5f) {

		}
		
		this.mLastMotionX = x; 
		this.mLastMotionY = y; 
		this.mLastMotionTime = aTime; 
	}

	static float dp2px(Context context, float dpSize) {
        Context c = context;
        Resources r;

        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }

        float value = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpSize, r.getDisplayMetrics()));
        return value;
    }

    private static final byte[] readAssetToByteArray(Context context, String name) {
        byte[] data = null;

        InputStream is = null;
        try {
            is = context.getAssets().open(name);

            ByteArrayOutputStream baos = new ByteArrayOutputStream(307200);

            byte[] buffer = new byte[204800];
            int len;
            while ((len = is.read(buffer)) >= 0) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();
            baos.close();
        } catch (IOException e) {

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }

        return data;
    }

	private static class HandWriteHandler extends Handler {

		HandWriteView mHolder;

		HandWriteHandler(HandWriteView holder) {
			this.mHolder = holder;
		}

		@Override
		public void handleMessage(Message msg) {
			if (mHolder.mOnHandWriteListener != null) {
				mHolder.mOnHandWriteListener.onHandWrite(mHolder, mHolder.mMatcher, mHolder.mResult);
			}
		}
	}

    /**
     * 手写识别接口
     *
     */
    public interface OnHandWriteListener {

        /**
         * 落笔
         *
         */
        void onStrokeBegan(HandWriteView view);

        /**
         * 起笔
         *
         */
        void onStrokeEnd(HandWriteView view);

        /**
         * 识别结果
         *
         * @param results
         */
        void onHandWrite(HandWriteView view, StrokesMatcher matcher, Character[] results);

    }

}
