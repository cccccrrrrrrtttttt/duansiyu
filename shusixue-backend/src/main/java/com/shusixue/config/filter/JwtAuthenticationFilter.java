package com.shusixue.config.filter;

import com.shusixue.entity.SysUser;
import com.shusixue.service.SysUserService;
import com.shusixue.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT认证过滤器，每次请求先经过这里，解析token并设置认证信息
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SysUserService sysUserService;

    // 核心修复：手动编写构造函数，并在 SysUserService 参数前加上 @Lazy
    public JwtAuthenticationFilter(JwtUtil jwtUtil, @Lazy SysUserService sysUserService) {
        this.jwtUtil = jwtUtil;
        this.sysUserService = sysUserService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求头获取token，格式：Bearer token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            // 没有token，直接放行，交给Security后续处理
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 截取token本体（去掉 "Bearer " 前缀）
        token = token.substring(7);

        // 3. 校验token是否有效
        if (!jwtUtil.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 解析token获取用户信息
        String username = jwtUtil.getUsernameFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        Long userId = jwtUtil.getUserIdFromToken(token);

        // 5. 构建一个简单的用户对象（不查数据库，避免再次触发循环依赖）
        // 这里只存必要的ID、用户名和角色，后续Controller需要详细信息时再查库
        SysUser simpleUser = new SysUser();
        simpleUser.setId(userId);
        simpleUser.setUsername(username);
        simpleUser.setRole(role);

        // 6. 设置认证信息到Security上下文
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                simpleUser,
                null,
                // 注意：Security规范要求角色必须加 "ROLE_" 前缀
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. 放行请求，进入Controller
        filterChain.doFilter(request, response);
    }
}