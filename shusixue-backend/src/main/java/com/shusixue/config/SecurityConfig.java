package com.shusixue.config;

import com.shusixue.config.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Spring Security核心配置类，适配6.x新版本写法
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // 开启注解权限控制
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器，用于密码加密和校验
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器，登录必备
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 核心安全过滤链配置（Spring Security 6.x 标准lambda写法）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1. 关闭csrf，前后端分离项目不需要
        http.csrf(csrf -> csrf.disable());
        // 新增：允许跨域
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 2. 关闭session，用JWT无状态认证
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 3. 配置接口放行规则
        http.authorizeHttpRequests(auth -> auth
                // 允许OPTIONS预检请求
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 登录、注册接口放行，不需要认证
                .requestMatchers("/api/user/register", "/api/user/login").permitAll()
                // ==================== Knife4j/Swagger 完整路径放行 ====================
                // Knife4j主页面
                .requestMatchers("/doc.html").permitAll()
                // Swagger UI相关资源
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                // OpenAPI文档接口
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                // Swagger资源文件
                .requestMatchers("/swagger-resources/**").permitAll()
                // Knife4j静态资源
                .requestMatchers("/knife4j/**").permitAll()
                // Webjars资源（Knife4j使用的前端依赖）
                .requestMatchers("/webjars/**").permitAll()
                // ==================== Knife4j/Swagger 路径结束 ====================
                // 其他所有接口都需要认证
                .anyRequest().authenticated()
        );

        // 4. 把JWT过滤器添加到Security过滤器链中
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * CORS配置源，用于Spring Security
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许所有来源
        config.addAllowedOriginPattern("*");
        // 允许所有请求头
        config.addAllowedHeader("*");
        // 允许所有请求方法
        config.addAllowedMethod("*");
        // 允许携带凭证
        config.setAllowCredentials(true);
        // 预检请求的有效期
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
