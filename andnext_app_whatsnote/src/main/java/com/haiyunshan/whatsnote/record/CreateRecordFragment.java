package com.haiyunshan.whatsnote.record;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import club.andnext.utils.SoftInputUtils;
import com.google.android.material.textfield.TextInputLayout;
import com.haiyunshan.whatsnote.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateRecordFragment extends Fragment {

    public static final String KEY_ID       = "record.id";
    public static final String KEY_PARENT   = "record.callback";
    public static final String KEY_NAME     = "record.name";
    public static final String KEY_HINT     = "record.hint";

    protected TextInputLayout textInputLayout;
    protected EditText nameEdit;

    protected View cancelBtn;
    protected View okBtn;

    public CreateRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        {
            textInputLayout = view.findViewById(R.id.text_input_layout);
            textInputLayout.setHint(getArguments().getString(KEY_HINT));

            nameEdit = view.findViewById(R.id.edit_name);
            nameEdit.setHint(getArguments().getString(KEY_HINT));
            nameEdit.setText(getArguments().getString(KEY_NAME));

            nameEdit.setSelection(0, nameEdit.length());

            cancelBtn = view.findViewById(R.id.btn_cancel);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });

            okBtn = view.findViewById(R.id.btn_ok);
            okBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameEdit.getText().toString();
                    if (!TextUtils.isEmpty(name)) {

                        Intent intent = new Intent();
                        intent.putExtras(getArguments());
                        intent.putExtra(KEY_NAME, name);

                        Activity context = getActivity();
                        context.setResult(Activity.RESULT_OK, intent);

                        context.finish();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        nameEdit.post(new Runnable() {
            @Override
            public void run() {
                SoftInputUtils.show(getActivity(), nameEdit);
            }
        });
    }
}
