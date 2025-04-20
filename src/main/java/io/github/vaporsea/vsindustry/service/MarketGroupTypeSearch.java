package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.domain.AbstractSearch;
import io.github.vaporsea.vsindustry.domain.Item;
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
public class MarketGroupTypeSearch extends AbstractSearch<Item> {
    
    private String search;
    private boolean marketGroupSearch = true;
    
    @Override
    protected Predicate toPredicateInternal(Root<Item> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (search != null && !search.isEmpty()) {
            predicates.add(criteriaBuilder.like(root.get("name"), "%" + search + "%"));
        }
        
        if (marketGroupSearch) {
            predicates.add(criteriaBuilder.isNotNull(root.get("marketGroupId")));
        }
        
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
