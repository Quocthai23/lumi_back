package com.lumiere.app.utils;

import java.security.SecureRandom;

public class CodeUtils {
    public  static   String resolveCodeFromName(String name,int size, SecureRandom random) {
        String base = SlugUtils.toSlug(name).replace("-", "").toUpperCase();
        if (base.length() > 12) base = base.substring(0, 12);
        String suffix = randomAlphaNum(size,random);
        String code = base + "-" + suffix;
        int guard = 0;
        return code;
    }

    public  static   String randomAlphaNum(int len,SecureRandom random) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
