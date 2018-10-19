package com.haiyunshan.whatsnote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import club.andnext.navigation.NavigationHelper;
import club.andnext.utils.GsonUtils;
import com.haiyunshan.preview.ExtensionDataset;
import com.haiyunshan.preview.PreviewEntity;
import com.haiyunshan.whatsnote.preview.BasePreviewFragment;
import com.haiyunshan.whatsnote.preview.PlainTextPreviewFragment;

public class PreviewActivity extends AppCompatActivity {

    static ExtensionDataset extensionDataset = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        {
            NavigationHelper.attach(this);
        }

        if (extensionDataset == null) {
            extensionDataset = GsonUtils.fromJson(this, "preview/extension_ds.json", ExtensionDataset.class);
        }

        BasePreviewFragment f = null;

        Intent intent = this.getIntent();
        PreviewEntity entity = PreviewEntity.create(this, intent);
        this.setTitle(entity.getDisplayName());

        if (TextUtils.isEmpty(entity.getUri())) {

        } else {
            String fragment = this.getFragment(entity);

            f = createFragment(fragment);
            if (f == null) {
                f = new PlainTextPreviewFragment();
            }
        }

        if (f == null) {
            if (!TextUtils.isEmpty(entity.getExtraText())) {
                f = new PlainTextPreviewFragment();
            }
        }

        if (f != null) {

            f.setArguments(entity);

            FragmentManager fm = this.getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.replace(android.R.id.content, f);
            t.commit();

        }
    }

    BasePreviewFragment createFragment(String fragment) {
        BasePreviewFragment f = null;

        try {
            Class clz = Class.forName(fragment);
            f = (BasePreviewFragment)(clz.newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return f;
    }

    String getFragment(PreviewEntity entity) {

        String fragment = PlainTextPreviewFragment.class.getSimpleName();

        {
            String ext = getExtension(entity);
            ext = ext.toLowerCase();
            ExtensionDataset.ExtensionEntity e = null;
            if (extensionDataset != null) {
                e = extensionDataset.accept(ext);
            }

            if (e != null) {
                fragment = e.getFragment();
            }

        }

        {
            String a = PlainTextPreviewFragment.class.getName();
            String b = PlainTextPreviewFragment.class.getSimpleName();

            fragment = a.substring(0, a.length() - b.length()) + fragment;
        }

        return fragment;
    }

    String getExtension(PreviewEntity entity) {
        String uri = entity.getUri();
        int index = uri.lastIndexOf('.');
        if (index <= 0) {
            return "";
        }

        return uri.substring(index);
    }



}
