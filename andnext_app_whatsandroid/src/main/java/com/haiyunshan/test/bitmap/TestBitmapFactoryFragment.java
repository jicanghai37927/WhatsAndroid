package com.haiyunshan.test.bitmap;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.whatsandroid.R;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestBitmapFactoryFragment extends Fragment {

    public TestBitmapFactoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_bitmap_factory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btn_test_in_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testInBitmap(v);
            }
        });
    }

    public void testInBitmap(View view) {
        File file = Environment.getExternalStorageDirectory();
        file = new File(file, "Pictures/山兔Mini.rtfd/IMG_0051.JPG");
        String uriString = Uri.fromFile(file).toString();
        int width = 768;
        int height = 527;

        BitmapFactory.Options options = new BitmapFactory.Options();

        {
            options.inSampleSize = 2;
        }

        {
            int w = (int) (width * 0.8);
            int h = (int) (height * 0.8);
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            options.inBitmap = bitmap;
        }

        try {

            Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(Uri.parse(uriString)), null, options);
            Log.v("AA", "bitmap width = " + bitmap.getWidth() + ", height = " + bitmap.getHeight());

            if (options.inBitmap != null) {
                Log.v("AA", "in bitmap width = " + options.inBitmap.getWidth() + ", height = " + options.inBitmap.getHeight());
            }
            bitmap.recycle();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
