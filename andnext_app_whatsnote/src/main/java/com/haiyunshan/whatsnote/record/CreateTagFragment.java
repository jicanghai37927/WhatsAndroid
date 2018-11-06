package com.haiyunshan.whatsnote.record;


import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import club.andnext.utils.ColorUtils;
import club.andnext.utils.GsonUtils;
import club.andnext.utils.SoftInputUtils;
import com.google.gson.annotations.SerializedName;
import com.haiyunshan.record.TagEntity;
import com.haiyunshan.record.TagUtils;
import com.haiyunshan.whatsnote.R;
import club.andnext.widget.CircleColorButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTagFragment extends Fragment implements View.OnClickListener {

    public static final String KEY_COLOR    =  "tag.color";
    public static final String KEY_NAME     =  "tag.name";

    View cancelBtn;
    View okBtn;

    ImageView targetView;
    EditText editName;

    CircleColorButton checkedTag;
    LinearLayout colorLayout;

    static int color = Color.TRANSPARENT;
    static int[] tagColors;

    public CreateTagFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_create_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.cancelBtn = view.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(this);
        this.okBtn = view.findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(this);

        this.targetView = view.findViewById(R.id.iv_target);
        this.editName = view.findViewById(R.id.edit_name);

        this.colorLayout = view.findViewById(R.id.color_layout);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (tagColors == null) {
            tagColors = createColors(getActivity());
        }

        {
            editName.post(new Runnable() {
                @Override
                public void run() {
                    editName.requestFocus();
                    SoftInputUtils.show(getActivity(), editName);
                }
            });
        }

        {
            ColorClickListener listener = new ColorClickListener();

            for (int c : tagColors) {
                int resource = R.layout.layout_tag_color_button;
                CircleColorButton button = (CircleColorButton)(getLayoutInflater().inflate(resource, colorLayout, false));
                button.setColor(c);
                button.setReplaceColor(TagUtils.getReplaceColor());
                button.setOnClickListener(listener);

                colorLayout.addView(button);
                if (c == color) {
                    checkedTag = button;
                }
            }

            if (checkedTag != null) {
                checkedTag.setChecked(true);
            }
        }

        {
            this.setTarget(checkedTag);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == cancelBtn) {
            getActivity().onBackPressed();
        } else if (v == okBtn){
            ok();
        }
    }

    void ok() {

        Activity context = getActivity();
        SoftInputUtils.hide(context);

        String name = editName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            context.onBackPressed();
            return;
        }

        if (TagUtils.exist(name)) {
            context.onBackPressed();
            return;
        }

        Intent intent = new Intent();
        intent.putExtras(getArguments());

        intent.putExtra(KEY_NAME, name);
        intent.putExtra(KEY_COLOR, ColorUtils.format(color));

        context.setResult(Activity.RESULT_OK, intent);

        context.finish();
    }

    void setTarget(CircleColorButton button) {

        int color = (button == null)? Color.TRANSPARENT: button.getColor();

        targetView.setImageResource(TagUtils.getDrawable(color));
        targetView.setImageTintList(ColorStateList.valueOf(TagUtils.getDisplayColor(color)));

    }

    static int[] createColors(Context context) {
        int[] colors = null;
        ColorDataset ds = GsonUtils.fromJson(context, "tag/color_ds.json", ColorDataset.class);
        if (ds != null && ds.list != null && !ds.list.isEmpty()) {
            int size = ds.list.size();
            colors = new int[size + 1];
            colors[0] = Color.TRANSPARENT;

            for (int i = 0; i < size; i++) {
                colors[i + 1] = ColorUtils.parse(ds.list.get(i).color);
            }
        }

        if (colors == null) {
            colors = new int[] {
                    0x00000000,
                    0xfffd3c2f,
                    0xfffd9600,
                    0xfffdcd01,
                    0xff4bda65,
                    0xff007bff,
                    0xff5755d7,
                    0xff8e8e95
            };
        }

        return colors;
    }

    /**
     *
     */
    private class ColorClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            CircleColorButton button = (CircleColorButton)v;
            if (checkedTag != button) {
                if (checkedTag != null) {
                    checkedTag.setChecked(false);
                }

                checkedTag = button;
                checkedTag.setChecked(true);

                color = checkedTag.getColor();

                setTarget(button);
            }
        }
    }

    /**
     *
     */
    private static class ColorDataset {

        @SerializedName("list")
        ArrayList<ColorEntry> list;
    }

    /**
     *
     */
    private static class ColorEntry {

        @SerializedName("color")
        String color;
    }
}
