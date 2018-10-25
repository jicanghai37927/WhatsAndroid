package club.andnext.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {

    public static final byte[] encrypt(String text, String password) throws Exception {

        return encrypt(text.getBytes("utf-8"), password);

    }

    public static final byte[] encrypt(byte[] data, String password) throws Exception {

        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(getKey(password), "AES/CBC/PKCS5PADDING");

        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");

        // 初始化加密器
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // 加密
        return cipher.doFinal(data);
    }

    public static final byte[] decrypt(byte[] data, String password) throws Exception {

        // 创建AES秘钥
        SecretKeySpec key = new SecretKeySpec(getKey(password), "AES/CBC/PKCS5PADDING");

        // 创建密码器
        Cipher cipher = Cipher.getInstance("AES");

        // 初始化解密器
        cipher.init(Cipher.DECRYPT_MODE, key);

        // 解密
        return cipher.doFinal(data);
    }

    private static final byte[] getKey(String password) {
        return password.getBytes();
    }
}
