package com.shusixue;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shusixue.mapper") // 扫描mapper包，必须加！
public class ShusixueBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShusixueBackendApplication.class, args);
    }

}