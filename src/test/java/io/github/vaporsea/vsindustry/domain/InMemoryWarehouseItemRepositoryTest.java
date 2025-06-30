package io.github.vaporsea.vsindustry.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryWarehouseItemRepositoryTest {

    private InMemoryWarehouseItemRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryWarehouseItemRepository();
    }

    @Test
    void testSaveAndFindById() {
        // Given
        Long itemId = 123L;
        WarehouseItem item = WarehouseItem.builder()
                .itemId(itemId)
                .quantity(10L)
                .costPerItem(100.0)
                .build();

        // When
        repository.save(item);
        Optional<WarehouseItem> foundItem = repository.findById(itemId);

        // Then
        assertTrue(foundItem.isPresent());
        assertEquals(itemId, foundItem.get().getItemId());
        assertEquals(10L, foundItem.get().getQuantity());
        assertEquals(100.0, foundItem.get().getCostPerItem());
    }

    @Test
    void testFindByIdNonExistent() {
        // When
        Optional<WarehouseItem> foundItem = repository.findById(999L);

        // Then
        assertFalse(foundItem.isPresent());
    }

    @Test
    void testSaveAll() {
        // Given
        WarehouseItem item1 = WarehouseItem.builder()
                .itemId(1L)
                .quantity(10L)
                .costPerItem(100.0)
                .build();
        WarehouseItem item2 = WarehouseItem.builder()
                .itemId(2L)
                .quantity(20L)
                .costPerItem(200.0)
                .build();
        List<WarehouseItem> items = Arrays.asList(item1, item2);

        // When
        repository.saveAll(items);

        // Then
        assertEquals(2, repository.count());
        assertTrue(repository.findById(1L).isPresent());
        assertTrue(repository.findById(2L).isPresent());
    }

    @Test
    void testFindAll() {
        // Given
        WarehouseItem item1 = WarehouseItem.builder()
                .itemId(1L)
                .quantity(10L)
                .costPerItem(100.0)
                .build();
        WarehouseItem item2 = WarehouseItem.builder()
                .itemId(2L)
                .quantity(20L)
                .costPerItem(200.0)
                .build();
        repository.save(item1);
        repository.save(item2);

        // When
        List<WarehouseItem> allItems = repository.findAll();

        // Then
        assertEquals(2, allItems.size());
        assertTrue(allItems.stream().anyMatch(item -> item.getItemId().equals(1L)));
        assertTrue(allItems.stream().anyMatch(item -> item.getItemId().equals(2L)));
    }

    @Test
    void testFindAllById() {
        // Given
        WarehouseItem item1 = WarehouseItem.builder()
                .itemId(1L)
                .quantity(10L)
                .costPerItem(100.0)
                .build();
        WarehouseItem item2 = WarehouseItem.builder()
                .itemId(2L)
                .quantity(20L)
                .costPerItem(200.0)
                .build();
        repository.save(item1);
        repository.save(item2);

        // When
        List<WarehouseItem> foundItems = repository.findAllById(Arrays.asList(1L, 3L, 2L));

        // Then
        assertEquals(2, foundItems.size());
        assertTrue(foundItems.stream().anyMatch(item -> item.getItemId().equals(1L)));
        assertTrue(foundItems.stream().anyMatch(item -> item.getItemId().equals(2L)));
    }

    @Test
    void testDeleteById() {
        // Given
        WarehouseItem item = WarehouseItem.builder()
                .itemId(1L)
                .quantity(10L)
                .costPerItem(100.0)
                .build();
        repository.save(item);
        assertTrue(repository.findById(1L).isPresent());

        // When
        repository.deleteById(1L);

        // Then
        assertFalse(repository.findById(1L).isPresent());
    }

    @Test
    void testDelete() {
        // Given
        WarehouseItem item = WarehouseItem.builder()
                .itemId(1L)
                .quantity(10L)
                .costPerItem(100.0)
                .build();
        repository.save(item);
        assertTrue(repository.findById(1L).isPresent());

        // When
        repository.delete(item);

        // Then
        assertFalse(repository.findById(1L).isPresent());
    }

    @Test
    void testDeleteAll() {
        // Given
        WarehouseItem item1 = WarehouseItem.builder()
                .itemId(1L)
                .quantity(10L)
                .costPerItem(100.0)
                .build();
        WarehouseItem item2 = WarehouseItem.builder()
                .itemId(2L)
                .quantity(20L)
                .costPerItem(200.0)
                .build();
        repository.save(item1);
        repository.save(item2);
        assertEquals(2, repository.count());

        // When
        repository.deleteAll();

        // Then
        assertEquals(0, repository.count());
    }
}