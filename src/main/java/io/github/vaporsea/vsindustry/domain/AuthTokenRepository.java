package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Matt Maurer <br>
 * @since 6/8/2024
 */
@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
}
