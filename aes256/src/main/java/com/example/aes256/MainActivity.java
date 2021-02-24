package com.example.aes256;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            byte[] pkey = generatePkey("zhijunhong", "123456", "1111");
            String base64EncryptStr = AESUtils.aesEncryptStr("我是明文 ", pkey, AESUtils.IV);    //密文
            Log.i(TAG, "encryptStr: " + base64EncryptStr + "\n");

            String decodeStr = AESUtils.aesDecodeStr3(base64EncryptStr, pkey, AESUtils.IV);
            Log.i(TAG, "decodeStr: " + decodeStr + "\n");

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成秘钥
     *
     * @param username
     * @param password
     * @param random
     * @return
     * @throws NoSuchAlgorithmException
     */
    private byte[] generatePkey(String username, String password, String random) throws NoSuchAlgorithmException {
        String mD5Str = MD5Utility.getMD5DefaultEncode(username + random + password);
        return Sha256Utils.getSHA256ByteArray(random + mD5Str);
    }
}
