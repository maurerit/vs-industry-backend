package io.github.vaporsea.vsindustry.client;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@RequiredArgsConstructor
@Component
public class OAuth2HeaderInterceptor implements ClientHttpRequestInterceptor {
    
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws
            IOException {
        request.getHeaders().add("Authorization", "Bearer " + JwtTokenHolder.getToken());
        return execution.execute(request, body);
    }
}
