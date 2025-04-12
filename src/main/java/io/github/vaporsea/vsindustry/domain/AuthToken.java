package io.github.vaporsea.vsindustry.domain;

import java.time.ZonedDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/8/2024
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_tokens")
public class AuthToken {
    @Id
    private String principal;

    private String token;

    private String refreshToken;

    private ZonedDateTime createdAt;

    private ZonedDateTime expiresAt;
}
