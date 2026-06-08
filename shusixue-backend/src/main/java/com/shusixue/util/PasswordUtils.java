package com.shusixue.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码工具类 - 用于生成和验证BCrypt密码
 */
public class PasswordUtils {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 生成BCrypt加密密码
     */
    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 验证密码
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }

    public static void main(String[] args) {
        // 生成 admin123 的 BCrypt 哈希
        String hash = encode("admin123");
        System.out.println("Hash: " + hash);
        System.out.println("Matches: " + matches("admin123", hash));

        // 也生成 teacher123 和 student123 的
        System.out.println("teacher123: " + encode("teacher123"));
        System.out.println("student123: " + encode("student123"));
    }
}
