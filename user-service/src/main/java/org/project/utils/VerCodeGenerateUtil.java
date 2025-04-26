package org.project.utils;

import java.security.SecureRandom;
import java.util.Random;

public class VerCodeGenerateUtil {
    //邮箱字符串提取，去除容易混淆的几个字符比如0,o,~
    private static final String SYMBOLS = "23456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new SecureRandom();

    /**
     * 限定范围内，随机生成四位验证码
     *
     * @return 生成的验证码
     */
    public static String getVerCode() {
        char[] nonceChars = new char[4];
        for (int i = 0; i < nonceChars.length; i++) {
            nonceChars[i] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }

        return new String(nonceChars);
    }
}
