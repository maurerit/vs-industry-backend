package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.util.StringUtils;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProductSearch extends AbstractSearch<Product> {
    
    private String search;
    
    @Override
    protected Predicate toPredicateInternal(Root<Product> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder) {
        if (StringUtils.hasText(search)) {
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + search.toLowerCase() + "%"
            );
        }
        return null;
    }
}
