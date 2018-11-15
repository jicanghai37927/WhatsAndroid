package com.haiyunshan.whatsnote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import club.andnext.utils.GsonUtils;
import com.haiyunshan.whatsnote.preview.dataset.ExtensionDataset;
import com.haiyunshan.whatsnote.preview.entity.PreviewMessage;
import com.haiyunshan.whatsnote.preview.BasePreviewFragment;
import com.haiyunshan.whatsnote.preview.PlainTextPreviewFragment;

public class PreviewActivity extends AppCompatActivity {

    static final String TAG = PreviewActivity.class.getSimpleName();

    static ExtensionDataset extensionDataset = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (extensionDataset == null) {
            extensionDataset = GsonUtils.fromJson(this, "preview/extension_ds.json", ExtensionDataset.class);
        }

        BasePreviewFragment f = null;

        Intent intent = this.getIntent();
        PreviewMessage entity = PreviewMessage.create(this, intent);
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

        {
            f.setArguments(entity);
        }

        if (f.canPreview(this)) {

            FragmentManager fm = this.getSupportFragmentManager();
            FragmentTransaction t = fm.beginTransaction();
            t.replace(android.R.id.content, f);
            t.commit();

        } else {


        }

        Log.v(TAG, "uri = " + entity.getUri());
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

    String getFragment(PreviewMessage entity) {

        String fragment = PlainTextPreviewFragment.class.getSimpleName();

        {
            String ext = entity.getExtension();
            ext = ext.toLowerCase();
            ExtensionDataset.ExtensionEntry e = null;
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

}
