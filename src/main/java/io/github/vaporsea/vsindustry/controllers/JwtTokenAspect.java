package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.client.JwtTokenHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class JwtTokenAspect {
    
    private final HttpServletRequest request;
    
    public JwtTokenAspect(HttpServletRequest request) {
        this.request = request;
    }
    
    @Before("within(@org.springframework.web.bind.annotation.RestController *)")
    public void captureJwtToken() {
        if (request.getCookies() != null) {
            Arrays.stream(request.getCookies())
                  .filter(cookie -> "EVEJWT".equals(cookie.getName()))
                  .findFirst()
                  .ifPresent(cookie -> {
                      JwtTokenHolder.setToken(cookie.getValue());
                      log.debug("Captured EVEJWT token: {}", cookie.getValue());
                  });
        }
    }
    
    @After("within(@org.springframework.web.bind.annotation.RestController *)")
    public void clearJwtToken() {
        JwtTokenHolder.clear();
        log.debug("Cleared EVEJWT token");
    }
}
