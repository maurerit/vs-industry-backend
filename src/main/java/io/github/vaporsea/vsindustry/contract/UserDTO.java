package io.github.vaporsea.vsindustry.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long characterId;
    
    private String characterName;
    
    private List<String> roles;
    
    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;
}
