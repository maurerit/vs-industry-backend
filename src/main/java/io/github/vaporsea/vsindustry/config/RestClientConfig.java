/*
 * MIT License
 *
 * Copyright (c) 2025 VaporSea
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.vaporsea.vsindustry.config;

import java.time.Duration;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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
    
    @Value("${vsindustry.userAgent.appName}")
    private String appName;
    
    @Value("${vsindustry.userAgent.gitRepoLocation}")
    private String gitRepoLocation;
    
    @Value("${vsindustry.userAgent.userEmail}")
    private String userEmail;
    
    @Value("${vsindustry.default.principal}")
    private String characterName;
    
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
                   .defaultHeader(HttpHeaders.USER_AGENT,
                           String.format("%s/1.0.0 (%s; +%s) eve:%s", appName, userEmail, gitRepoLocation,
                                   characterName))
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
                                 new LoggingInterceptor(LoggerFactory.getLogger("io.github.vaporsea.login"),
                                         new DefaultLogFormatter()))
                         .build();
    }
}
