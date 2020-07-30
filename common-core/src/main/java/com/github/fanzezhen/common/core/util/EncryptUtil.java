package com.github.fanzezhen.common.core.util;

import java.security.MessageDigest;

public class EncryptUtil {

    public static String md5(String plaintext) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plaintext.getBytes());
            byte[] bytes = md.digest();

            int c;

            StringBuilder buf = new StringBuilder();
            for (byte aByte : bytes) {
                c = aByte;
                if (c < 0) {
                    c += 256;
                }
                if (c < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(c));
            }
            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
