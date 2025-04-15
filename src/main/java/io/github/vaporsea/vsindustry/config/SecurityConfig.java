package io.github.vaporsea.vsindustry.config;

import io.github.vaporsea.vsindustry.controllers.JwtTokenFilter;
import io.github.vaporsea.vsindustry.security.CustomAccessDeniedHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Matt Maurer <br>
 * @since 6/17/2024
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@Configuration
public class SecurityConfig {
    
    @Bean
    @ConditionalOnProperty(name = "oauth2.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter) throws Exception {
        http
                .authorizeHttpRequests(
                        authorize -> authorize.anyRequest().authenticated())
                .oauth2Login();
        
        http.addFilterBefore(
                jwtTokenFilter,
                UsernamePasswordAuthenticationFilter.class
        );
        
        http.exceptionHandling()
            .accessDeniedHandler(new CustomAccessDeniedHandler());
        
        return http.build();
    }
    
    @Bean
    @ConditionalOnProperty(name = "oauth2.enabled", havingValue = "false")
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, JwtTokenFilter jwtTokenFilter)
            throws Exception {
        http.authorizeHttpRequests(
                    authorizer -> authorizer.anyRequest().authenticated())
            .csrf().disable();
        
        http.addFilterBefore(
                jwtTokenFilter,
                UsernamePasswordAuthenticationFilter.class
        );
        
        http.exceptionHandling()
            .accessDeniedHandler(new CustomAccessDeniedHandler());
        
        return http.build();
    }
}
