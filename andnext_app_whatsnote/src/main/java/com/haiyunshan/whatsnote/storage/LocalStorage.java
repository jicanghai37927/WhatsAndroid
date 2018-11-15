package com.haiyunshan.whatsnote.storage;

import android.content.Context;
import androidx.annotation.NonNull;

import java.io.File;

public class LocalStorage {

    public static final String PATH_NOTE_DIR    = "WhatsNote/note";
    public static final String PATH_EXTRACT_DIR = "WhatsNote/extract";

    public static final String PATH_ENTRANCE_DIR = "Preference/entrance";

    public static final String PATH_CAMERA_PHOTO_DIR = "Camera/photo";

    static LocalStorage instance;

    public static final LocalStorage getInstance() {
        if (instance == null) {
            instance = new LocalStorage();
        }

        return instance;
    }

    private LocalStorage() {

    }

    public File getDirectory(Context context, @NonNull String path) {

        File dir = context.getExternalFilesDir(null);
        dir = new File(dir, path);
        dir.mkdirs();

        return dir;
    }
}
