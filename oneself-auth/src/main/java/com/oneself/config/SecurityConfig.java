package com.oneself.config;

import com.oneself.filter.JwtAuthenticationTokenFilter;
import com.oneself.model.vo.ResponseVO;
import com.oneself.utils.JacksonUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author liuhuan
 * date 2025/9/10
 * packageName com.oneself.config
 * className SecurityConfig
 * description
 * version 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private static final ResponseVO<String> UNAUTHORIZED = ResponseVO.failure("未认证，请登录", HttpStatus.UNAUTHORIZED);
    private static final ResponseVO<String> FORBIDDEN = ResponseVO.failure("无权限访问", HttpStatus.FORBIDDEN);

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain 配置（Spring Security 6.1+ Lambda 风格）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 关闭 CSRF（无状态 JWT）
                .csrf(AbstractHttpConfigurer::disable)

                // 不使用 Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // 登录接口允许匿名
                        .anyRequest().authenticated()               // 其他接口需要认证
                )

                // 自定义异常处理（返回 JSON）
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.getWriter().write(JacksonUtils.toJsonString(UNAUTHORIZED));
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.getWriter().write(JacksonUtils.toJsonString(FORBIDDEN));
                        })
                )

                // 注册 JWT 过滤器（在用户名密码过滤器之前执行）
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Spring Security 6.1+ 显式暴露 AuthenticationManager Bean
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }
}