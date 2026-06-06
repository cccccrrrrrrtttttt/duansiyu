package com.shusixue;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenPwd {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin123"));
        System.out.println(encoder.encode("teacher123"));
        System.out.println(encoder.encode("student123"));
    }
}
