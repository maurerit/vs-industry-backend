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

package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.UserDTO;
import io.github.vaporsea.vsindustry.util.JwtTokenUtil;
import io.github.vaporsea.vsindustry.domain.Role;
import io.github.vaporsea.vsindustry.domain.RoleRepository;
import io.github.vaporsea.vsindustry.domain.User;
import io.github.vaporsea.vsindustry.domain.UserRepository;
import io.github.vaporsea.vsindustry.domain.UserRole;
import io.github.vaporsea.vsindustry.domain.UserRoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final ModelMapper modelMapper;
    
    public boolean hasUsers() {
        return userRepository.count() > 0;
    }
    
    public void addRoleToUser(UserDTO user, String role) {
        userRepository.findByCharacterId(user.getCharacterId())
                      .orElseThrow(() -> new UsernameNotFoundException(
                              "User not found with id: " + user.getCharacterId()));
        
        Role foundRole = roleRepository.findByRoleName(role)
                                       .orElseThrow(
                                               () -> new EntityNotFoundException("Role not found with name: " + role));
        
        userRoleRepository.save(UserRole.builder()
                                        .id(UserRole.UserRoleId.builder()
                                                               .roleId(foundRole.getRoleId())
                                                               .characterId(user.getCharacterId())
                                                               .build())
                                        .build());
    }
    
    public Page<UserDTO> getUsers(PageRequest pageRequest) {
        org.springframework.data.domain.Page<User> users = userRepository.findAll(pageRequest);
        
        return new Page<>(users.getNumber(), users.getTotalPages(), users.getTotalElements(),
                users.map(user -> modelMapper.map(user, UserDTO.class)).getContent());
    }
    
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByCharacterName(username)
                                       .orElseThrow(() -> new UsernameNotFoundException(
                                               "User not found with username: " + username));
        
        List<GrantedAuthority> authorities = roleRepository.findByCharacterName(username)
                                                           .stream()
                                                           .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                                                           .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(foundUser.getCharacterName(), "", authorities);
    }
    
    public UserDTO saveUser(UserDTO userDto) {
        User user = userRepository.findByCharacterId(userDto.getCharacterId())
                                  .orElse(User.builder().createdAt(ZonedDateTime.now()).build());
        
        user.setCharacterId(userDto.getCharacterId());
        user.setCharacterName(userDto.getCharacterName());
        user.setUpdatedAt(ZonedDateTime.now());
        userRepository.save(user);
        
        return modelMapper.map(user, UserDTO.class);
    }
    
    public UserDTO getUser(Long characterId) {
        return userRepository.findById(characterId)
                             .map(user -> modelMapper.map(user, UserDTO.class))
                             .orElseThrow(
                                     () -> new UsernameNotFoundException("User not found with id: " + characterId));
    }
    
    public UserDTO me(String jwtToken) {
        String username = jwtTokenUtil.getUsername(jwtToken);
        
        User user = userRepository.findByCharacterName(username)
                                  .orElseThrow(() -> new UsernameNotFoundException(
                                          "User not found with username: " + username));
        List<Role> roles = roleRepository.findByCharacterName(username);
        
        return UserDTO.builder()
                      .characterId(user.getCharacterId())
                      .characterName(username)
                      .roles(roles.stream().map(Role::getRoleName).toList())
                      .createdAt(user.getCreatedAt())
                      .updatedAt(user.getUpdatedAt())
                      .build();
    }
}
