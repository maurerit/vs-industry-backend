package io.github.vaporsea.vsindustry.domain;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public abstract class AbstractSearch<T> implements Specification<T> {
    
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        return toPredicateInternal(root, query, criteriaBuilder);
    }
    
    protected abstract Predicate toPredicateInternal(Root<T> root, CriteriaQuery<?> query,
            CriteriaBuilder criteriaBuilder);
}
