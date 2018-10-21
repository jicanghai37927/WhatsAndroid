package com.haiyunshan.demo.handwrite;


import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import club.andnext.handwrite.HandWriteView;
import com.haiyunshan.whatsandroid.R;
import hanzilookup.data.StrokesMatcher;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class HandWriteDemoFragment extends Fragment {

    TextView resultView;
    HandWriteView handWriteView;
    View clearBtn;

    public HandWriteDemoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_hand_write_demo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            this.resultView = view.findViewById(R.id.tv_result);
        }

        {
            this.handWriteView = view.findViewById(R.id.hand_write_view);
            handWriteView.setOnHandWriteListener(new HandWriteView.OnHandWriteListener() {
                @Override
                public void onStrokeBegan(HandWriteView view) {

                }

                @Override
                public void onStrokeEnd(HandWriteView view) {

                }

                @Override
                public void onHandWrite(HandWriteView view, StrokesMatcher matcher, Character[] results) {

                    String text = Arrays.toString(results);
                    resultView.setText(text);

                }
            });
        }

        {
            this.clearBtn = view.findViewById(R.id.btn_clear);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handWriteView.clear();
                    resultView.setText("识别结果：");
                }
            });
        }
    }
}
