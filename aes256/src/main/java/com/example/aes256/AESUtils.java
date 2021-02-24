package com.example.aes256;

import android.util.Log;


import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * @author zhijunhong
 * @ClassName: AESUtils
 * @Description: aes对称加密解密工具类, 注意密钥不能随机生机, 不同客户端调用可能需要考虑不同Provider
 */
public class AESUtils {
    public static final String TAG = AESUtils.class.getSimpleName();

    /***默认向量常量**/
    public static final String IV = "1234567890123456";

    /**
     * 使用PKCS7Padding填充必须添加一个支持PKCS7Padding的Provider
     * 类加载的时候就判断是否已经有支持256位的Provider,如果没有则添加进去
     */
    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * @param content 需要加密的原内容
     * @param pkey    密匙
     * @param
     * @return
     */
    public static byte[] aesEncrypt(String content, byte[] pkey, String IV) {
        try {
            //SecretKey secretKey = generateKey(pkey);
            //byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec skey = new SecretKeySpec(pkey, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");// "算法/加密/填充"
            IvParameterSpec iv = new IvParameterSpec(IV.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, skey, iv);//初始化加密器
            byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
            return encrypted; // 加密
        } catch (Exception e) {
            Log.i(TAG,"aesEncrypt() method error:", e);
        }
        return null;
    }

    /**
     * 获得密钥
     *
     * @param secretKey
     * @return
     * @throws Exception
     */
    private static SecretKey generateKey(String secretKey) throws Exception {
        //防止linux下 随机生成key
        Provider p = Security.getProvider("SUN");
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", p);
        secureRandom.setSeed(secretKey.getBytes());
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(secureRandom);
        // 生成密钥
        return kg.generateKey();
    }

    /**
     * @param content 加密前原内容
     * @param iv
     * @return base64EncodeStr   aes加密完成后内容
     * @throws
     * @Title: aesEncryptStr
     * @Description: aes对称加密
     */
    public static String aesEncryptStr(String content, byte[] pkey, String iv) {
        byte[] aesEncrypt = aesEncrypt(content, pkey, iv);
        System.out.println("加密后的byte数组:" + Arrays.toString(aesEncrypt));
        String base64EncryptStr = Base64Utils.encode(aesEncrypt);
        System.out.println("加密后 base64EncodeStr:" + base64EncryptStr);
        return base64EncryptStr;
    }

    /**
     * @param content base64处理过的字符串
     * @param pkey    密匙
     * @param
     * @return String    返回类型
     * @throws Exception
     * @throws
     * @Title: aesDecodeStr
     * @Description: 解密 失败将返回NULL
     */
    public static String aesDecodeStr(String content, byte[] pkey, String IV) throws Exception {
        try {
            System.out.println("待解密内容:" + content);
//            byte[] base64DecodeStr = Base64Utils.decode(content);
//            System.out.println("base64DecodeStr:" + Arrays.toString(base64DecodeStr));
            byte[] aesDecode = aesDecode(content.getBytes(), pkey, IV);
            System.out.println("aesDecode:" + Arrays.toString(aesDecode));
            if (aesDecode == null) {
                return null;
            }
            String result;
            result = new String(aesDecode, "UTF-8");
            System.out.println("aesDecode result:" + result);
            return result;
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
            throw new Exception("解密异常");
        }
    }


    /**
     * @param base64EncryptStr base64处理过的字符串
     * @param pkey    密匙
     * @param
     * @return String    返回类型
     * @throws Exception
     * @throws
     * @Title: aesDecodeStr
     * @Description: 解密 失败将返回NULL
     */
    public static String aesDecodeStr3(String base64EncryptStr, byte[] pkey, String IV) throws Exception {
        byte[] base64DecodeStr = Base64Utils.decode(base64EncryptStr);
        byte[] aesDecode = aesDecode(base64DecodeStr, pkey, IV);
        if (aesDecode == null) {
            return null;
        }
        String result;
        result = new String(aesDecode, "UTF-8");
        return result;
    }

    /**
     * 解密
     *
     * @param encryptStr 解密前的byte数组
     * @param pkey    密匙
     * @param IV
     * @return result  解密后的byte数组
     * @throws Exception
     */
    public static byte[] aesDecode(byte[] encryptStr, byte[] pkey, String IV) throws Exception {
        //SecretKey secretKey = generateKey(pkey);
        //byte[] enCodeFormat = secretKey.getEncoded();
        SecretKeySpec skey = new SecretKeySpec(pkey, "AES");
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");// 创建密码器
        cipher.init(Cipher.DECRYPT_MODE, skey, iv);// 初始化解密器
        byte[] result = cipher.doFinal(encryptStr);
        return result; // 解密

    }
}