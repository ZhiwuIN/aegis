package com.aegis.utils;

import java.util.Random;

public class RandomDataUtils {
    private static final Random RANDOM = new Random();

    /**
     * 生成随机 11 位手机号（以 13、15、18、17 等开头）
     */
    public static String getRandomPhone() {
        String[] head = {"13", "15", "17", "18", "19"};
        StringBuilder sb = new StringBuilder();
        sb.append(head[RANDOM.nextInt(head.length)]);
        for (int i = 0; i < 9; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成随机邮箱
     */
    public static String getRandomEmail() {
        String[] domains = {"qq.com", "163.com", "gmail.com", "mobaijun.com"};
        String alphabet = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        }
        sb.append("@").append(domains[RANDOM.nextInt(domains.length)]);
        return sb.toString();
    }

    /**
     * 生成随机昵称
     */
    public static String getRandomNickname() {
        String[] prefix = {"开发者", "系统", "测试", "用户", "极客"};
        return prefix[RANDOM.nextInt(prefix.length)] + "_" + (RANDOM.nextInt(9000) + 1000);
    }
}