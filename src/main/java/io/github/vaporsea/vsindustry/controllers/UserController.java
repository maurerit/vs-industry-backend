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
    
    @RolesAllowed("ROLE_ADMIN")
    @GetMapping
    public Page<UserDTO> getUsers(int page, int pageSize) {
        return userService.getUsers(PageRequest.of(page, pageSize));
    }
    
    @RolesAllowed("ROLE_ADMIN")
    @PostMapping
    public UserDTO saveUser(@RequestBody UserDTO userDto) {
        return userService.saveUser(userDto);
    }
    
    @RolesAllowed("ROLE_ADMIN")
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
