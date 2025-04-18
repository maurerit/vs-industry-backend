package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.UserDTO;
import io.github.vaporsea.vsindustry.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class FirstLoginController {
    
    private final UserService userService;
    
    @GetMapping("/has-users")
    public ResponseEntity<Void> hasUsers() {
        if (!userService.hasUsers()) {
            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/create-first-user")
    public ResponseEntity<Void> createFirstUser(@RequestParam Long characterId,
            @RequestParam String characterName) {
        if (userService.hasUsers()) {
            return ResponseEntity.badRequest().build();
        }
        
        UserDTO createdUser =
                userService.saveUser(UserDTO.builder().characterId(characterId).characterName(characterName).build());
        userService.addRoleToUser(createdUser, "ROLE_ADMIN");
        
        return ResponseEntity.ok().build();
    }
}
