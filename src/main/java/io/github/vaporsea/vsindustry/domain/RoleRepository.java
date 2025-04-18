package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    
    @Query(nativeQuery = true,
            value = """
                                SELECT r.*
                                  FROM roles r
                                  join user_roles ur on ur.role_id = r.role_id
                                  join users u on u.character_id = ur.character_id
                                 where u.character_name = :characterName
                    """)
    List<Role> findByCharacterName(String characterName);
    
    Optional<Role> findByRoleName(String name);
}
