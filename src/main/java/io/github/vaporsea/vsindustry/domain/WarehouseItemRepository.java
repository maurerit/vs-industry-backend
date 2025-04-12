package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
public interface WarehouseItemRepository extends JpaRepository<WarehouseItem, Long> {
}
