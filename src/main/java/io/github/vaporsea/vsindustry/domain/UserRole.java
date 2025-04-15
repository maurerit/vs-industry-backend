package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "user_roles")
public class UserRole {
    
    @EmbeddedId
    private UserRoleId id;
    
    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;
    
    @Embeddable
    @Data
    public static class UserRoleId {
        
        private Long characterId;
        
        private Long roleId;
    }
}