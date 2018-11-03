package com.haiyunshan.test.hierarchy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import club.andnext.navigation.NavigationHelper;
import club.andnext.utils.AlertDialogUtils;
import com.haiyunshan.whatsandroid.R;
import me.kareluo.ui.OptionMenu;
import me.kareluo.ui.OptionMenuView;
import me.kareluo.ui.PopupMenuView;

import java.util.Arrays;

public class TestViewHierarchyActivity extends AppCompatActivity {

    View contentLayout;
    TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_test_view_hierarchy);

        {
            this.contentLayout = findViewById(R.id.container);
            this.contentView = findViewById(R.id.tv_content);
        }

        {
            findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TestViewHierarchyActivity.class);
                    startActivity(intent);
                }
            });

            findViewById(R.id.btn_info).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    updateInfo();
                }
            });

            findViewById(R.id.btn_dialog).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

            findViewById(R.id.btn_popupwindow).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showPopupWindow(v);
                }
            });

            findViewById(R.id.btn_hello).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "您好！", Toast.LENGTH_SHORT).show();
                }
            });


        }

        {
            this.updateInfo();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.updateInfo();
    }

    void updateInfo() {
        updateRootInfo();
    }

    void updateRootInfo() {
        View root = contentLayout.getRootView();


        StringBuilder sb = new StringBuilder();

        ViewParent parent = root.getParent();
        while (parent != null) {
            sb.insert(0, parent.toString() + "\n");
            parent = parent.getParent();
        }

        travel(sb, 1, root);

        contentView.setText(sb);
    }

    void travel(StringBuilder sb, int indent, View view) {
        for (int i = 0; i < indent; i++) {
            sb.append("  ");
        }

        sb.append(view.getClass().getSimpleName());
        if (view instanceof ViewGroup) {
            sb.append("(");
            sb.append(((ViewGroup)view).getChildCount());
            sb.append(")");
        }
        sb.append('\n');

        if (view == contentLayout) {
            return;
        }

        if (view instanceof ViewGroup) {
            ViewGroup layout = (ViewGroup)view;
            int count = layout.getChildCount();
            for (int i = 0; i < count; i++) {
                travel(sb, (indent + 1), layout.getChildAt(i));
            }
        }
    }

    void updateParentInfo() {

        StringBuilder sb = new StringBuilder();

        sb.append("设置的ContentView = " + contentLayout.getClass().getName());
        sb.insert(0, '\n');

        ViewParent parent = contentLayout.getParent();
        while (parent != null) {
            String text = getInfo(parent);
            sb.insert(0, '\n');
            sb.insert(0, text);

            parent = parent.getParent();
        }

        contentView.setText(sb);

    }

    String getInfo(ViewParent parent) {
        String name = parent.getClass().getName();

        if (parent instanceof ViewGroup) {
            ViewGroup view = (ViewGroup)parent;
            name += "[" + view.getId() + "]" + "[子控件数 = " + view.getChildCount() + "]";
        } else if (parent instanceof View) {
            View view = (View)parent;
            name += "[" + view.getId() + "]" + "[只是View]";
        }

        return name;
    }

    AlertDialog showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("测试对话框");

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    updateInfo();
                } else {
                    dialog.dismiss();
                }
            }
        };
        builder.setPositiveButton("更新信息", listener);
        builder.setNegativeButton(android.R.string.cancel, listener);

        final AlertDialog dialog = builder.show();
        dialog.getWindow().setDimAmount(0);
        dialog.setCanceledOnTouchOutside(true);

        {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    {
                        updateInfo();

                        contentView.append("\n");
                    }

                    {
                        Window d = dialog.getWindow();
                        Window a = TestViewHierarchyActivity.this.getWindow();

                        contentView.append("Dialog Window = " + d.toString());
                        contentView.append("\n");

                        contentView.append("Activity Window = " + a.toString());
                        contentView.append("\n");

                        contentView.append("\n");
                    }

                    {
                        Window d = dialog.getWindow();
                        Window a = TestViewHierarchyActivity.this.getWindow();

                        contentView.append("Dialog WindowManager = " + d.getWindowManager().toString());
                        contentView.append("\n");

                        contentView.append("Activity WindowManager = " + a.getWindowManager().toString());
                        contentView.append("\n");

                        WindowManager wm = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
                        contentView.append("Context WindowManager = " + wm.toString());
                        contentView.append("\n");
                    }
                }
            });
        }

        return dialog;
    }

    void showPopupWindow(View view) {

        // 根据menu资源文件创建
        final PopupMenuView menuView = new PopupMenuView(this);
        menuView.setMenuItems(Arrays.asList(
                new OptionMenu("复制"), new OptionMenu("转发到朋友圈"),
                new OptionMenu("收藏"), new OptionMenu("翻译"),
                new OptionMenu("删除")));


// 设置点击监听事件
        menuView.setOnMenuClickListener(new OptionMenuView.OnOptionMenuClickListener() {
            @Override
            public boolean onOptionMenuClick(int position, OptionMenu menu) {

                {
                    updateInfo();

                    contentView.append("\n");
                }

                {
                    ViewParent d = getRootParent(menuView.getContentView());
                    ViewParent a = getRootParent(contentLayout);

                    contentView.append("PopupMenu RootParent = " + d.toString());
                    contentView.append("\n");

                    contentView.append("Activity RootParent = " + a.toString());
                    contentView.append("\n");

                    contentView.append("\n");
                }

                return false;
            }
        });


        menuView.setFocusable(false);
        menuView.setOutsideTouchable(true);

// 显示在mButtom控件的周围
        menuView.show(view);

//        updateInfo();

//        contentView.requestLayout();

//        showDialog();
    }

    ViewParent getRootParent(View view) {

        ViewParent r;

        ViewParent parent = view.getParent();
        r = parent;

        while (parent != null) {
            r = parent;
            parent = parent.getParent();
        }

        return r;
    }
}
