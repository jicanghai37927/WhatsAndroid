package com.haiyunshan.storage;

import android.content.Context;
import androidx.annotation.NonNull;
import com.haiyunshan.whatsnote.WhatsApp;

import java.io.File;

public class LocalStorage {

    public static final String PATH_NOTE_DIR    = "WhatsNote/note";
    public static final String PATH_EXTRACT_DIR = "WhatsNote/extract";

    static LocalStorage instance;

    public static final LocalStorage getInstance() {
        if (instance == null) {
            instance = new LocalStorage();
        }

        return instance;
    }

    private LocalStorage() {

    }

    public File getDirectory(@NonNull String path) {
        Context context = WhatsApp.getContext();

        return getDirectory(context, path);
    }

    public File getDirectory(Context context, @NonNull String path) {

        File dir = context.getExternalFilesDir(null);
        dir = new File(dir, path);
        dir.mkdirs();

        return dir;
    }
}
