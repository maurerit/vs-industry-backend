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

import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.UserDTO;
import io.github.vaporsea.vsindustry.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {
    
    private final UserService userService;
    
    @RolesAllowed("ADMIN")
    @GetMapping
    public Page<UserDTO> getUsers(int page, int pageSize) {
        return userService.getUsers(PageRequest.of(page, pageSize));
    }
    
    @RolesAllowed("ADMIN")
    @PostMapping
    public UserDTO saveUser(@RequestBody UserDTO userDto) {
        return userService.saveUser(userDto);
    }
    
    @RolesAllowed("ADMIN")
    @GetMapping("/{characterId}")
    public UserDTO getUser(@PathVariable Long characterId) {
        return userService.getUser(characterId);
    }
    
    @GetMapping("/me")
    public UserDTO getCurrentUser(HttpServletRequest request) {
        final Optional<Cookie> cookie =
                Arrays.stream(request.getCookies()).filter(c -> c.getName().equals("EVEJWT")).findFirst();
        
        return cookie.map(value -> userService.me(value.getValue().trim())).orElse(null);
    }
}
