package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.domain.AbstractSearch;
import io.github.vaporsea.vsindustry.domain.IndustryJob;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class IndustryJobStatusSearch extends AbstractSearch<IndustryJob> {
    
    private Boolean finished;
    
    @Override
    protected Predicate toPredicateInternal(Root<IndustryJob> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (finished != null) {
            if (finished) {
                predicates.add(criteriaBuilder.equal(root.get("status"), "delivered"));
            }
            else {
                predicates.add(criteriaBuilder.equal(root.get("status"), "active"));
            }
        }
        
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
