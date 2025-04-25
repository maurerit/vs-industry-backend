package io.github.vaporsea.vsindustry.config;

import java.time.Duration;

import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import io.github.vaporsea.vsindustry.client.OAuth2HeaderInterceptor;
import io.github.vaporsea.vsindustry.client.DefaultLogFormatter;
import io.github.vaporsea.vsindustry.client.LoggingInterceptor;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@Configuration
public class RestClientConfig {
    
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder.build();
    }
    
    @Bean
    RestClientCustomizer restClientCustomizer(OAuth2HeaderInterceptor oAuth2HeaderInterceptor) {
        return builder -> {
            int connectTimeout = 10000;
            int readTimeout = 30000;
            
            ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                    .withConnectTimeout(Duration.ofMillis(connectTimeout))
                    .withReadTimeout(Duration.ofMillis(readTimeout));
            
            builder.requestFactory(new BufferingClientHttpRequestFactory(ClientHttpRequestFactories.get(settings)))
                   .baseUrl("https://esi.evetech.net/")
                   .requestInterceptor(new LoggingInterceptor(LoggerFactory.getLogger("io.github.vaporsea.eve"),
                           new DefaultLogFormatter()))
                   .requestInterceptor(oAuth2HeaderInterceptor);
        };
    }
    
    @Bean
    RestClient tokenRefreshClient() {
        return RestClient.builder()
                         .baseUrl("https://login.eveonline.com/")
                         .requestFactory(new BufferingClientHttpRequestFactory(ClientHttpRequestFactories.get(
                                 ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(Duration.ofMillis(10000))
                                                                          .withReadTimeout(Duration.ofMillis(30000)))))
                         .requestInterceptor(
                                 new LoggingInterceptor(LoggerFactory.getLogger("io.github.vaporsea.eve.login"),
                                         new DefaultLogFormatter()))
                         .build();
    }
}
