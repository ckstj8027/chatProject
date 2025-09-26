package com.example.chatserver.common.configs;

import com.example.chatserver.common.auth.JwtAuthFilter;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfigs {

    private final JwtAuthFilter jwtAuthFilter;
    @Bean
    public SecurityFilterChain myFilter(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                //Cross-Origin Resource Sharing의 약자로, 교차 출처 리소스 공유를 의미합니다. 웹에서 CORS는 한 도메인에서 제공되는 웹 페이지가 다른 도메인에서 제공하는 리소스를 요청할 수 있도록 하는 보안 메커니즘
                // 프론트는 로컬호스트 3000 인데 백엔드는 8080 이니까 연결시킬라고 설정
                .csrf(AbstractHttpConfigurer::disable)  // 굳이 여기서 안해도 다른 방법이많아서 권장 x
                .httpBasic(AbstractHttpConfigurer::disable)// http basic 비활성화
                .authorizeHttpRequests(a->a.requestMatchers("/member/create","/member/doLogin","/connect/**").permitAll().anyRequest().authenticated())
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))// 세션방식 비활성화
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }



    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://frontend-three-gray-94.vercel.app"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);

        return source;
    }

    @Bean

    public PasswordEncoder makePassword(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
