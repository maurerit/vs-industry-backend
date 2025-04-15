package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "roles")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;
    
    private String roleName;
    
    private ZonedDateTime createdAt;
    
    private ZonedDateTime updatedAt;
}
