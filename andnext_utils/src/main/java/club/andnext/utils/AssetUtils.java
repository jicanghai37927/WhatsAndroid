package club.andnext.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 工具类
 *
 */
public class AssetUtils {


	public static final String getString(Context context, String name) {
	    return getString(context, name, "utf-8");
    }

	public static final String getString(Context context, String name, String encoding) {
		byte[] data = getByteArray(context, name);
		if (data == null) {
			return null; 
		}

		String str = null; 
		try {
			str = new String(data, encoding);
		} catch (UnsupportedEncodingException e) {
		} 

		return str; 
	}

	public static final byte[] getByteArray(Context context, String name) {
		byte[] data = null; 
		
		InputStream is = null; 
		try {
			is = context.getAssets().open(name);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(307200); 
			
			byte[] buffer = new byte[204800]; 
			int len;
			while ((len = is.read(buffer)) >= 0) {
				baos.write(buffer, 0, len);
			}
			
			data = baos.toByteArray(); 
			baos.close(); 
		} catch (IOException e) {
			
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			} 
		}
		
		return data; 
	}
	
}
