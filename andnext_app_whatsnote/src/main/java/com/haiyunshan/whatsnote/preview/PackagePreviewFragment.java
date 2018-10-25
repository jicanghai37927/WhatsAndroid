package com.haiyunshan.whatsnote.preview;


import android.content.ComponentName;
import android.content.Context;
import android.content.pm.*;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import club.andnext.utils.PackageUtils;
import com.haiyunshan.preview.PreviewEntity;
import com.haiyunshan.whatsnote.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class PackagePreviewFragment extends BasePreviewFragment implements View.OnClickListener {

    TextView textView;

    View installBtn;

    public PackagePreviewFragment() {

    }

    @Override
    public boolean canPreview(Context context) {
        PreviewEntity data = this.getEntity();
        String path = data.getFilePath(context);
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_package_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        {
            this.textView = view.findViewById(R.id.tv_content);
        }

        {
            this.installBtn = view.findViewById(R.id.btn_install);
            installBtn.setOnClickListener(this);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreviewEntity entity = this.getEntity();
        CharSequence text = this.getPackageInfo(getActivity(), entity.getFilePath(getActivity()));

        textView.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v == installBtn) {
            String path = getEntity().getFilePath(getActivity());

            PackageUtils.install(getActivity(), new File(path));
        }
    }

    CharSequence getPackageInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        int flags = PackageManager.GET_ACTIVITIES
                | PackageManager.GET_CONFIGURATIONS
                | PackageManager.GET_GIDS
                | PackageManager.GET_INSTRUMENTATION
                | PackageManager.GET_INTENT_FILTERS
                | PackageManager.GET_META_DATA
                | PackageManager.GET_PERMISSIONS
                | PackageManager.GET_PROVIDERS
                | PackageManager.GET_RECEIVERS
                | PackageManager.GET_SERVICES
                | PackageManager.GET_SHARED_LIBRARY_FILES
                | PackageManager.GET_SIGNATURES
                | PackageManager.GET_URI_PERMISSION_PATTERNS
                | PackageManager.GET_DISABLED_COMPONENTS
                | PackageManager.GET_DISABLED_UNTIL_USED_COMPONENTS
                | PackageManager.GET_UNINSTALLED_PACKAGES;

        PackageInfo pi = pm.getPackageArchiveInfo(path, flags);
        if (pi == null) {
            return "";
        }

        pi.applicationInfo.sourceDir = path;
        pi.applicationInfo.publicSourceDir = path;

        StringBuilder sb = new StringBuilder();

        {
            String name = pi.applicationInfo.loadLabel(pm).toString();
            sb.append(name);
            sb.append('\n');

            sb.append(pi.versionName);
            sb.append('\n');

            sb.append(String.valueOf(pi.versionCode));
            sb.append('\n');

            sb.append('\n');
        }

        {
            String[] array = pi.requestedPermissions;
            if (array != null) {
                for (String i : array) {
                    sb.append(i);
                    sb.append('\n');
                }
            }

            sb.append('\n');
        }

        {
            ActivityInfo[] array = pi.activities;
            if (array != null) {
                for (ActivityInfo i: array) {
                    sb.append(i.name);
                    sb.append('\n');
                }
            }

            sb.append('\n');
        }


        {
            ProviderInfo[] array = pi.providers;
            if (array != null) {
                for (ProviderInfo i: array) {
                    sb.append(i.name);
                    sb.append('\n');
                }
            }

            sb.append('\n');
        }

        return sb;
    }

}
