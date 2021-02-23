package com.example.aes256;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utility {
    public static String getMD5DefaultEncode(String val) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        StringBuffer buffer = new StringBuffer();
        byte[] result = new byte[0];
        result = digest.digest(val.getBytes());
        for (byte b : result) {
            int number = b & 0xff;
            String numberStr = Integer.toHexString(number);
            if (numberStr.length() == 1) {
                buffer.append("0");
            }
            buffer.append(numberStr);
        }
        return buffer.toString().toUpperCase();
    }

    public static String getMD5(String val) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        StringBuffer buffer = new StringBuffer();
        byte[] result = new byte[0];
        try {
            result = digest.digest(val.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (byte b : result) {
            int number = b & 0xff;
            String numberStr = Integer.toHexString(number);
            if (numberStr.length() == 1) {
                buffer.append("0");
            }
            buffer.append(numberStr);
        }
        return buffer.toString().toUpperCase();
    }

    public static String getMD5Lower(String val) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        StringBuffer buffer = new StringBuffer();
        byte[] result = new byte[0];
        try {
            result = digest.digest(val.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (byte b : result) {
            int number = b & 0xff;
            String numberStr = Integer.toHexString(number);
            if (numberStr.length() == 1) {
                buffer.append("0");
            }
            buffer.append(numberStr);
        }
        return buffer.toString().toLowerCase();
    }
}
