package com.example.shop.configuration;

import com.example.shop.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;




@Configuration // đánh dấu đây là một lớp cấu hình Spring
@EnableWebSecurity // bật tính năng bảo mật web
@EnableMethodSecurity // bật tính năng bảo mật dựa trên phương thức

public class SecuriyConfig {


    // Khai báo một mảng các URL công khai, không yêu cầu xác thực.
    private final String[] PUBLIC_URLS = {"/user", "/auth/login", "/auth/introspect", "/auth/logout", "/auth/refresh"};

    // Inject giá trị của thuộc tính jwt.signerKey từ file cấu hình vào biến signerKey.
    @Value("${jwt.signerKey}")
    private String signerKey;

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(request ->
                request.requestMatchers(HttpMethod.POST, PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated());

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(customJwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter()))
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.exceptionHandling(exceptionHandlingConfigurer ->
                exceptionHandlingConfigurer.authenticationEntryPoint(authenticationEntryPoint()));

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            ErrorCode error = ErrorCode.UNAUTHORIZED; // Sử dụng mã lỗi UNAUTHORIZED từ enum ErrorCode
            String errorCode = String.valueOf(error.getCode()); // Lấy mã lỗi từ ErrorCode
            String errorMessage = error.getMessage(); // Lấy thông điệp từ ErrorCode
            response.getWriter().write("{\"code\": \"" + errorCode + "\", \"error\": \"" + errorMessage + "\"}");
        };
    }
    // Tạo một bean JwtDecoder để giải mã JWT token.
//


}
