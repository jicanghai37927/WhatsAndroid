package club.andnext.utils;

import android.content.Context;

import android.util.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

public class GsonUtils {

    public static final String toJson(Object obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    public static final File toJson(Object obj, File file) {
        file.getParentFile().mkdirs();

        try {
            file.delete();
            file.createNewFile();

            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8");

            Gson gson = new Gson();
            gson.toJson(obj, writer);

            writer.close();
            fos.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final <T> T fromJson(String text, Class<T> classOfT) {

        Gson gson = new Gson();
        T ds = gson.fromJson(text, classOfT);

        return ds;
    }

    public static final <T> T fromJson(Context context, String path, Class<T> classOfT) {

        try {
            InputStream is = context.getAssets().open(path);
            InputStreamReader isr = new InputStreamReader(is, "utf-8");

            Gson gson = new Gson();
            T ds = gson.fromJson(isr, classOfT);

            isr.close();
            is.close();

            return ds;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static final <T> T fromJson(File file, Class<T> classOfT) {
        return fromJson(file, classOfT, null);
    }

    public static final <T> T fromJson(File file, Class<T> classOfT, Pair<Type, JsonDeserializer>... typeAdapter) {

        if (!file.exists()) {
            return null;
        }

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "utf-8");

            Gson gson;
            if (typeAdapter != null) {
                GsonBuilder builder = new GsonBuilder();
                for (Pair<Type, JsonDeserializer> p : typeAdapter) {
                    builder.registerTypeAdapter(p.first, p.second);
                }
                gson = builder.create();
            } else {
                gson = new Gson();
            }

            T ds = gson.fromJson(isr, classOfT);

            isr.close();
            fis.close();

            return ds;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}
