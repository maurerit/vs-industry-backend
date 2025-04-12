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
