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

package io.github.vaporsea.vsindustry.client;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.vaporsea.vsindustry.controllers.JwtTokenUtil;
import io.github.vaporsea.vsindustry.domain.AuthToken;
import io.github.vaporsea.vsindustry.domain.AuthTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class OAuth2HeaderInterceptor implements ClientHttpRequestInterceptor {
    
    @Value("${vsindustry.default.principal}")
    private String principal;
    
    @Value("${spring.security.oauth2.client.registration.eve.client-id}")
    private String clientId;
    
    @Value("${spring.security.oauth2.client.registration.eve.client-secret}")
    private String clientSecret;
    
    @Value("${spring.security.oauth2.client.provider.eve.token-uri}")
    private String tokenUri;
    
    private final AuthTokenRepository authTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final RestClient tokenRefreshClient;
    
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws
            IOException {
        // Try to get token from JwtTokenHolder first (for backward compatibility)
        String token = JwtTokenHolder.getToken();
        String refreshToken;
        
        // If no token in JwtTokenHolder, get it from AuthTokenRepository
        if (token == null) {
            Optional<AuthToken> optionalAuthToken = authTokenRepository.findById(principal);
            if (optionalAuthToken.isPresent()) {
                AuthToken currentAuthToken = optionalAuthToken.get();
                token = currentAuthToken.getToken();
                refreshToken = currentAuthToken.getRefreshToken();
                log.debug("Using token from AuthTokenRepository for principal: {}", principal);
                
                // Check if token is expired and refresh if needed
                if (!jwtTokenUtil.validate(token)) {
                    log.info("Token is expired, refreshing...");
                    try {
                        Map<String, Object> tokenResponse = refreshToken(refreshToken);
                        if (tokenResponse != null) {
                            token = (String) tokenResponse.get("access_token");
                            String newRefreshToken = (String) tokenResponse.get("refresh_token");
                            
                            // Update token in AuthTokenRepository
                            currentAuthToken.setToken(token);
                            currentAuthToken.setRefreshToken(newRefreshToken);
                            
                            // Calculate new expiry time
                            Integer expiresIn = (Integer) tokenResponse.get("expires_in");
                            ZonedDateTime expiresAt = ZonedDateTime.now().plusSeconds(expiresIn);
                            currentAuthToken.setExpiresAt(expiresAt);
                            
                            authTokenRepository.save(currentAuthToken);
                            log.info("Token refreshed successfully");
                        }
                        else {
                            log.error("Failed to refresh token");
                        }
                    }
                    catch (Exception e) {
                        log.error("Error refreshing token", e);
                    }
                }
            }
            else {
                log.warn("No token found in AuthTokenRepository for principal: {}", principal);
            }
        }
        
        if (token != null) {
            request.getHeaders().add("Authorization", "Bearer " + token);
        }
        
        return execution.execute(request, body);
    }
    
    /**
     * Refreshes the token using the refresh token.
     *
     * @param refreshToken the refresh token
     *
     * @return the response from the token endpoint, or null if the refresh failed
     */
    private Map<String, Object> refreshToken(String refreshToken) {
        try {
            // Create authorization header
            String auth = clientId + ":" + clientSecret;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            
            // Create form data
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "refresh_token");
            formData.add("refresh_token", refreshToken);
            
            String response = tokenRefreshClient.post()
                                                .uri(tokenUri)
                                                .header(HttpHeaders.HOST, "login.eveonline.com")
                                                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                                                .header(HttpHeaders.CONTENT_TYPE,
                                                        MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                                .header(HttpHeaders.USER_AGENT, "vs-industry/1.0.0")
                                                .body(formData)
                                                .retrieve()
                                                .body(String.class);
            
            // Parse response
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(response, new TypeReference<>() {
            });
        }
        catch (Exception e) {
            log.error("Error refreshing token", e);
            return null;
        }
    }
}
