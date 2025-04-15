package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.UserDTO;
import io.github.vaporsea.vsindustry.domain.RoleRepository;
import io.github.vaporsea.vsindustry.domain.User;
import io.github.vaporsea.vsindustry.domain.UserRepository;
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
    private final ModelMapper modelMapper;
    
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
        User user = userRepository.findByCharacterId(userDto.getCharacterId()).orElse(
                User.builder()
                    .createdAt(ZonedDateTime.now())
                    .build()
        );
        
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
}
