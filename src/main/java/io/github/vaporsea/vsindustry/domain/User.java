package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    
    @Id
    private Long characterId;
    
    private String characterName;
    
    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;
}
