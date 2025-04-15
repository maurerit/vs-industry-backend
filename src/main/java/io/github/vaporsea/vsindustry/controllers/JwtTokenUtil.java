package io.github.vaporsea.vsindustry.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vaporsea.vsindustry.contract.EveJwtDTO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtTokenUtil {
    
    public boolean validate(String token) {
        // Check if the token is expired
        String tokenBody = token.split("\\.")[1];
        String decodedBody = new String(java.util.Base64.getDecoder().decode(tokenBody));
        try {
            EveJwtDTO eveJwtDTO = new ObjectMapper().readValue(decodedBody, EveJwtDTO.class);
            long currentTime = System.currentTimeMillis() / 1000;
            return eveJwtDTO.getExp() > currentTime;
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }
    
    /**
     * Extracts the username (subject) from the JWT token.
     *
     * @param token the JWT token
     *
     * @return the username
     */
    public String getUsername(String token) {
        String tokenBody = token.split("\\.")[1];
        String decodedBody = new String(java.util.Base64.getDecoder().decode(tokenBody));
        try {
            EveJwtDTO eveJwtDTO = new ObjectMapper().readValue(decodedBody, EveJwtDTO.class);
            return eveJwtDTO.getName();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }
    
    /**
     * Extracts the roles from the JWT token.
     *
     * @param token the JWT token
     *
     * @return the roles
     */
    public List<SimpleGrantedAuthority> getRoles(String token) {
        String tokenBody = token.split("\\.")[1];
        String decodedBody = new String(java.util.Base64.getDecoder().decode(tokenBody));
        try {
            EveJwtDTO eveJwtDTO = new ObjectMapper().readValue(decodedBody, EveJwtDTO.class);
            return Arrays.stream(eveJwtDTO.getScp()).map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to parse JWT token", e);
        }
    }
}