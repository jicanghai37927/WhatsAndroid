package com.haiyunshan.whatsnote.directory;

import android.content.Context;
import androidx.annotation.NonNull;

import java.io.File;

public class DirectoryManager {

    public static final String DIR_NOTE     = "WhatsNote/note";
    public static final String DIR_EXTRACT  = "WhatsNote/extract";

    public static final String DIR_ENTRANCE = "Preference/entrance";

    public static final String DIR_CAMERA_PHOTO = "Camera/photo";

    public static final String DIR_EXPORT_PICTURE = "Export/Picture";

    static DirectoryManager instance;

    public static final DirectoryManager getInstance() {
        if (instance == null) {
            instance = new DirectoryManager();
        }

        return instance;
    }

    private DirectoryManager() {

    }

    public File getDirectory(Context context, @NonNull String path) {

        File dir = context.getExternalFilesDir(null);
        dir = new File(dir, path);
        dir.mkdirs();

        return dir;
    }
}
