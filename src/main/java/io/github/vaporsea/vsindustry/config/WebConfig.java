package io.github.vaporsea.vsindustry.config;

import io.github.vaporsea.vsindustry.filter.Slf4jRequestLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {
    @Bean
    public Slf4jRequestLoggingFilter logFilter() {
        Slf4jRequestLoggingFilter filter = new Slf4jRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setIncludePayload(true);
        return filter;
    }
}
