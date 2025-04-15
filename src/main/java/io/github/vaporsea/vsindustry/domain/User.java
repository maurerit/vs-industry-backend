package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    
    @Id
    private Long characterId;
    
    private String characterName;
    
    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;
}
