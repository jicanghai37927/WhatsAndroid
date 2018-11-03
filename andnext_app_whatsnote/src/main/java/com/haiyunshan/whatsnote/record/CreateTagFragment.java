package com.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import club.andnext.utils.ColorUtils;
import club.andnext.utils.UUIDUtils;
import com.haiyunshan.whatsnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateTagFragment extends Fragment {

    public static final String KEY_COLOR    =  "tag.color";
    public static final String KEY_NAME     =  "tag.name";

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

        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtras(getArguments());

                intent.putExtra(KEY_NAME, UUIDUtils.next().substring(0, 8));
                intent.putExtra(KEY_COLOR, ColorUtils.format(Color.BLUE));

                Activity context = getActivity();
                context.setResult(Activity.RESULT_OK, intent);

                context.finish();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
