package io.github.vaporsea.vsindustry.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author Matt Maurer <br>
 * @since 6/17/2024
 */
@Configuration
public class SecurityConfig {
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/product-setup", "/corp/*", "/product/*", "/warehouse");
    }
    
    @Bean
    @ConditionalOnProperty(name = "oauth2.enabled", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorize -> authorize.anyRequest().authenticated())
                .oauth2Login();
        return http.build();
    }
    
    @Bean
    @ConditionalOnProperty(name = "oauth2.enabled", havingValue = "false")
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        authorizer -> authorizer.anyRequest().permitAll())
                .csrf().disable();
        return http.build();
    }
}
