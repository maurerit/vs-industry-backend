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

import java.time.ZoneId;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.stereotype.Component;

import io.github.vaporsea.vsindustry.domain.AuthToken;
import io.github.vaporsea.vsindustry.domain.AuthTokenRepository;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/8/2024
 */
@RequiredArgsConstructor
@Component
public class PersistentOauth2AuthorizedClientService implements OAuth2AuthorizedClientService {
    
    private final AuthTokenRepository authTokenRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;
    
    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId,
            String principalName) {
        AuthToken authToken = authTokenRepository.findById(principalName).orElseThrow();
        
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
                authToken.getToken(), authToken.getCreatedAt().toInstant(), authToken.getExpiresAt().toInstant());
        OAuth2RefreshToken refreshToken =
                new OAuth2RefreshToken(authToken.getRefreshToken(), authToken.getCreatedAt().toInstant());
        
        return (T) new OAuth2AuthorizedClient(clientRegistrationRepository.findByRegistrationId(clientRegistrationId),
                principalName, accessToken, refreshToken);
    }
    
    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        AuthToken authToken = AuthToken.builder()
                                       .principal(principal.getName())
                                       .token(authorizedClient.getAccessToken().getTokenValue())
                                       .refreshToken(authorizedClient.getRefreshToken().getTokenValue())
                                       .createdAt(
                                               authorizedClient.getAccessToken().getIssuedAt().atZone(ZoneId.of("UTC")))
                                       .expiresAt(authorizedClient.getAccessToken()
                                                                  .getExpiresAt()
                                                                  .atZone(ZoneId.of("UTC"))).build();
        
        authTokenRepository.save(authToken);
    }
    
    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {
        authTokenRepository.delete(authTokenRepository.findById(principalName).orElseThrow());
    }
}
