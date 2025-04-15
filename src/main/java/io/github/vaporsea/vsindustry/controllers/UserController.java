package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.UserDTO;
import io.github.vaporsea.vsindustry.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@RolesAllowed("ROLE_ADMIN")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping
    public Page<UserDTO> getUsers(int page, int size) {
        return userService.getUsers(PageRequest.of(page, size));
    }
}
