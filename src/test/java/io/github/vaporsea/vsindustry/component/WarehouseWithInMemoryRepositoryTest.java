package io.github.vaporsea.vsindustry.component;

import io.github.vaporsea.vsindustry.domain.InMemoryWarehouseItemRepository;
import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseWithInMemoryRepositoryTest {

    private InMemoryWarehouseItemRepository warehouseItemRepository;
    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouseItemRepository = new InMemoryWarehouseItemRepository();
        warehouse = new Warehouse(warehouseItemRepository);
    }

    @Test
    void testGetWarehouseItem_ExistingItem() {
        // Given
        Long itemId = 123L;
        WarehouseItem existingItem = WarehouseItem.builder()
                .itemId(itemId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();
        warehouseItemRepository.save(existingItem);

        // When
        WarehouseItem result = warehouse.getWarehouseItem(itemId);

        // Then
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        assertEquals(5L, result.getQuantity());
        assertEquals(100.0, result.getCostPerItem());
    }

    @Test
    void testGetWarehouseItem_NonExistingItem() {
        // Given
        Long itemId = 123L;

        // When
        WarehouseItem result = warehouse.getWarehouseItem(itemId);

        // Then
        assertNotNull(result);
        assertEquals(itemId, result.getItemId());
        assertEquals(0L, result.getQuantity());
        assertEquals(0.0, result.getCostPerItem());
    }

    @Test
    void testRemoveItem_DefaultBehavior_NoNegativeQuantities() {
        // Given
        Long itemId = 123L;
        Long quantity = 10L;
        WarehouseItem existingItem = WarehouseItem.builder()
                .itemId(itemId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();
        warehouseItemRepository.save(existingItem);

        // When
        double result = warehouse.removeItem(itemId, quantity);

        // Then
        assertEquals(1000.0, result); // 10 * 100.0
        WarehouseItem updatedItem = warehouseItemRepository.findById(itemId).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(0L, updatedItem.getQuantity());
        assertEquals(100.0, updatedItem.getCostPerItem());
    }

    @Test
    void testRemoveItem_AllowNegativeQuantities_True() {
        // Given
        Long itemId = 123L;
        Long quantity = 10L;
        WarehouseItem existingItem = WarehouseItem.builder()
                .itemId(itemId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();
        warehouseItemRepository.save(existingItem);
        warehouse.setAllowNegativeQuantities(true);

        // When
        double result = warehouse.removeItem(itemId, quantity);

        // Then
        assertEquals(1000.0, result); // 10 * 100.0
        WarehouseItem updatedItem = warehouseItemRepository.findById(itemId).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(-5L, updatedItem.getQuantity());
        assertEquals(100.0, updatedItem.getCostPerItem());
    }

    @Test
    void testAddItem_NewItem() {
        // Given
        Long itemId = 123L;
        Long quantity = 10L;
        Double costPerItem = 100.0;

        // When
        warehouse.addItem(itemId, quantity, costPerItem);

        // Then
        WarehouseItem item = warehouseItemRepository.findById(itemId).orElse(null);
        assertNotNull(item);
        assertEquals(itemId, item.getItemId());
        assertEquals(quantity, item.getQuantity());
        assertEquals(costPerItem, item.getCostPerItem());
    }

    @Test
    void testAddItem_ExistingItem() {
        // Given
        Long itemId = 123L;
        WarehouseItem existingItem = WarehouseItem.builder()
                .itemId(itemId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();
        warehouseItemRepository.save(existingItem);

        // When
        warehouse.addItem(itemId, 10L, 200.0);

        // Then
        WarehouseItem updatedItem = warehouseItemRepository.findById(itemId).orElse(null);
        assertNotNull(updatedItem);
        assertEquals(itemId, updatedItem.getItemId());
        assertEquals(15L, updatedItem.getQuantity()); // 5 + 10
        // Rolling average: (100.0 * 5 + 200.0 * 10) / (5 + 10) = 166.67
        assertEquals(166.67, updatedItem.getCostPerItem(), 0.01);
    }
}