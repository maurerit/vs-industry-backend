package io.github.vaporsea.vsindustry.component;

import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import io.github.vaporsea.vsindustry.domain.WarehouseItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WarehouseTest {

    @Mock
    private WarehouseItemRepository warehouseItemRepository;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse(warehouseItemRepository);
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

        when(warehouseItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));

        // When
        double result = warehouse.removeItem(itemId, quantity);

        // Then
        assertEquals(1000.0, result); // 10 * 100.0
        verify(warehouseItemRepository).save(argThat(item -> 
            item.getItemId().equals(itemId) && 
            item.getQuantity() == 0L && 
            item.getCostPerItem().equals(100.0)
        ));
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

        when(warehouseItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        warehouse.setAllowNegativeQuantities(true);

        // When
        double result = warehouse.removeItem(itemId, quantity);

        // Then
        assertEquals(1000.0, result); // 10 * 100.0
        verify(warehouseItemRepository).save(argThat(item -> 
            item.getItemId().equals(itemId) && 
            item.getQuantity() == -5L && 
            item.getCostPerItem().equals(100.0)
        ));
    }

    @Test
    void testRemoveItem_AllowNegativeQuantities_False() {
        // Given
        Long itemId = 123L;
        Long quantity = 10L;
        WarehouseItem existingItem = WarehouseItem.builder()
                .itemId(itemId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();

        when(warehouseItemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        warehouse.setAllowNegativeQuantities(false);

        // When
        double result = warehouse.removeItem(itemId, quantity);

        // Then
        assertEquals(1000.0, result); // 10 * 100.0
        verify(warehouseItemRepository).save(argThat(item -> 
            item.getItemId().equals(itemId) && 
            item.getQuantity() == 0L && 
            item.getCostPerItem().equals(100.0)
        ));
    }

    @Test
    void testGettersAndSetters() {
        // Default value should be false
        assertFalse(warehouse.isAllowNegativeQuantities());
        
        // Set to true
        warehouse.setAllowNegativeQuantities(true);
        assertTrue(warehouse.isAllowNegativeQuantities());
        
        // Set back to false
        warehouse.setAllowNegativeQuantities(false);
        assertFalse(warehouse.isAllowNegativeQuantities());
    }
}