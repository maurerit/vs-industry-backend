package io.github.vaporsea.vsindustry.contract;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class UserDTO {
    
    private Long characterId;
    
    private String characterName;
    
    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;
}
