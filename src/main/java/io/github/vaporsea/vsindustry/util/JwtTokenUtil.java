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

package io.github.vaporsea.vsindustry.util;

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