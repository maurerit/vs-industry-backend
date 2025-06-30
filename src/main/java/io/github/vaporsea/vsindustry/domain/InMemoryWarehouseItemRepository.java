package io.github.vaporsea.vsindustry.domain;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * An in-memory implementation of the WarehouseItemRepository interface.
 * This implementation stores warehouse items in a HashMap.
 */
@Repository
public class InMemoryWarehouseItemRepository implements WarehouseItemRepository {

    private final Map<Long, WarehouseItem> warehouseItems = new HashMap<>();

    @Override
    public Optional<WarehouseItem> findById(Long id) {
        return Optional.ofNullable(warehouseItems.get(id));
    }

    @Override
    public <S extends WarehouseItem> S save(S entity) {
        warehouseItems.put(entity.getItemId(), entity);
        return entity;
    }

    @Override
    public <S extends WarehouseItem> List<S> saveAll(Iterable<S> entities) {
        List<S> savedEntities = new ArrayList<>();
        entities.forEach(entity -> savedEntities.add(save(entity)));
        return savedEntities;
    }

    @Override
    public boolean existsById(Long id) {
        return warehouseItems.containsKey(id);
    }

    @Override
    public List<WarehouseItem> findAll() {
        return List.copyOf(warehouseItems.values());
    }

    @Override
    public List<WarehouseItem> findAllById(Iterable<Long> ids) {
        List<WarehouseItem> result = new ArrayList<>();
        ids.forEach(id -> {
            WarehouseItem item = warehouseItems.get(id);
            if (item != null) {
                result.add(item);
            }
        });
        return result;
    }

    @Override
    public long count() {
        return warehouseItems.size();
    }

    @Override
    public void deleteById(Long id) {
        warehouseItems.remove(id);
    }

    @Override
    public void delete(WarehouseItem entity) {
        warehouseItems.remove(entity.getItemId());
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> ids) {
        ids.forEach(warehouseItems::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends WarehouseItem> entities) {
        entities.forEach(entity -> warehouseItems.remove(entity.getItemId()));
    }

    @Override
    public void deleteAll() {
        warehouseItems.clear();
    }

    // The following methods are required by the JpaRepository interface but are not used by the Warehouse component.
    // They are implemented with minimal functionality to satisfy the interface.

    @Override
    public List<WarehouseItem> findAll(Sort sort) {
        return findAll(); // Ignoring sort for simplicity
    }

    @Override
    public Page<WarehouseItem> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public <S extends WarehouseItem> Optional<S> findOne(Example<S> example) {
        throw new UnsupportedOperationException("Example queries not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public <S extends WarehouseItem> List<S> findAll(Example<S> example) {
        throw new UnsupportedOperationException("Example queries not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public <S extends WarehouseItem> List<S> findAll(Example<S> example, Sort sort) {
        throw new UnsupportedOperationException("Example queries not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public <S extends WarehouseItem> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Example queries not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public <S extends WarehouseItem> long count(Example<S> example) {
        throw new UnsupportedOperationException("Example queries not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public <S extends WarehouseItem> boolean exists(Example<S> example) {
        throw new UnsupportedOperationException("Example queries not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public <S extends WarehouseItem, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        throw new UnsupportedOperationException("Fluent queries not supported in InMemoryWarehouseItemRepository");
    }

    @Override
    public void flush() {
        // No-op for in-memory implementation
    }

    @Override
    public <S extends WarehouseItem> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends WarehouseItem> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<WarehouseItem> entities) {
        deleteAll(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> ids) {
        deleteAllById(ids);
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
    }

    @Override
    public WarehouseItem getOne(Long id) {
        return findById(id).orElse(null);
    }

    @Override
    public WarehouseItem getById(Long id) {
        return findById(id).orElse(null);
    }

    @Override
    public WarehouseItem getReferenceById(Long id) {
        return findById(id).orElse(null);
    }
}
