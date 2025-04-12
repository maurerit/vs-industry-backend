package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IndustryActivityProductRepository extends JpaRepository<IndustryActivityProduct, IndustryActivityProductKey> {
    IndustryActivityProduct findById_ProductTypeId(Long productId);
}
