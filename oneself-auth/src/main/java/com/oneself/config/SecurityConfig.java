package com.oneself.config;

import com.oneself.filter.JwtAuthenticationTokenFilter;
import com.oneself.resp.Resp;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
    private static final Resp<String> UNAUTHORIZED = Resp.failure("未认证，请登录", HttpStatus.UNAUTHORIZED);
    private static final Resp<String> FORBIDDEN = Resp.failure("无权限访问", HttpStatus.FORBIDDEN);

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS 配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许所有源（生产环境建议指定具体域名）
        configuration.setAllowedOriginPatterns(List.of("*"));
        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // 允许的请求头
        configuration.setAllowedHeaders(List.of("*"));
        // 允许发送凭证（Cookie、Authorization 等）
        configuration.setAllowCredentials(true);
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * SecurityFilterChain 配置（Spring Security 6.1+ Lambda 风格）
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 配置 CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 关闭 CSRF（无状态 JWT）
                .csrf(AbstractHttpConfigurer::disable)

                // 不使用 Session
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 授权规则
                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 预检请求允许匿名（CORS 预检）
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        // 登录和验证码接口允许匿名
                        .requestMatchers("/auth/login", "/auth/captcha").permitAll()
                        // Swagger/Knife4j 文档相关路径允许匿名
                        .requestMatchers(
                                "/doc.html",                    // Knife4j 文档页面
                                "/swagger-ui/**",               // Swagger UI
                                "/swagger-ui.html",            // Swagger UI 旧版
                                "/v3/api-docs/**",             // OpenAPI 3 文档
                                "/v2/api-docs/**",             // OpenAPI 2 文档（兼容）
                                "/swagger-resources/**",        // Swagger 资源
                                "/webjars/**",                 // 静态资源
                                "/favicon.ico"                 // 图标
                        ).permitAll()
                        .anyRequest().authenticated()          // 其他接口需要认证
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