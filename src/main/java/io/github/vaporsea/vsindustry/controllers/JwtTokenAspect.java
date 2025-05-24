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
