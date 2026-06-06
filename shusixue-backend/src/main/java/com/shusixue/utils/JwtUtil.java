package com.shusixue.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类，适配JJWT 0.12.x 新版本API
 */
@Component
public class JwtUtil {

    // 从配置文件读取密钥和过期时间
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 生成安全密钥
     */
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token
     * @param username 用户名
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT token
     */
    public String generateToken(String username, Long userId, String role) {
        return Jwts.builder()
                // 存入核心用户信息
                .subject(username)
                .claim("userId", userId)
                .claim("role", role)
                // 签发时间
                .issuedAt(new Date())
                // 过期时间
                .expiration(new Date(System.currentTimeMillis() + expiration))
                // 签名算法（新版本写法）
                .signWith(getSecretKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * 解析token，获取Claims（载荷信息）
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 解析token，获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 解析token，获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return getClaimsFromToken(token).get("userId", Long.class);
    }

    /**
     * 解析token，获取用户角色
     */
    public String getRoleFromToken(String token) {
        return getClaimsFromToken(token).get("role", String.class);
    }

    /**
     * 校验token是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 解析成功即有效
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // token过期、签名错误、格式错误都返回无效
            return false;
        }
    }
}