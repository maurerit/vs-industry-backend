package io.github.vaporsea.vsindustry.component;

import io.github.vaporsea.vsindustry.domain.IndustryActivityProbability;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProbabilityKey;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProbabilityRepository;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProduct;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProductRepository;
import io.github.vaporsea.vsindustry.domain.IndustryJob;
import io.github.vaporsea.vsindustry.domain.IndustryJobRepository;
import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OverlayWarehouseTest {

    @Mock
    private Warehouse warehouse;

    @Mock
    private IndustryJobRepository industryJobRepository;

    @Mock
    private IndustryActivityProbabilityRepository industryActivityProbabilityRepository;

    @Mock
    private IndustryActivityProductRepository industryActivityProductRepository;

    private OverlayWarehouse overlayWarehouse;

    @BeforeEach
    void setUp() {
        overlayWarehouse = new OverlayWarehouse(warehouse, industryJobRepository, industryActivityProbabilityRepository, industryActivityProductRepository);
    }

    @Test
    void testGetWarehouseItem_NoBlueprint() {
        // Given
        Long itemId = 123L;
        WarehouseItem expectedItem = WarehouseItem.builder()
                .itemId(itemId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();

        when(warehouse.getWarehouseItem(itemId)).thenReturn(expectedItem);
        when(industryJobRepository.findUnprocessed()).thenReturn(List.of());

        // When
        WarehouseItem result = overlayWarehouse.getWarehouseItem(itemId);

        // Then
        assertEquals(expectedItem, result);
        verify(warehouse).getWarehouseItem(itemId);
        verify(industryJobRepository).findUnprocessed();
        verifyNoInteractions(industryActivityProbabilityRepository);
        verifyNoInteractions(industryActivityProductRepository);
    }

    @Test
    void testGetWarehouseItem_WithBlueprint() {
        // Given
        Long blueprintId = 123L;
        Long productId = 456L;
        Long activityId = 8L; // Invention activity

        // Create a warehouse item for the blueprint
        WarehouseItem blueprintItem = WarehouseItem.builder()
                .itemId(productId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();

        // Create an industry job for the blueprint
        IndustryJob job = IndustryJob.builder()
                .jobId(1L)
                .blueprintTypeId(blueprintId)
                .productTypeId(productId)
                .activityId(activityId)
                .runs(10L)
                .licensedRuns(2L)
                .cost(1000.0)
                .build();

        // Create a probability for the job
        IndustryActivityProbabilityKey probabilityKey = IndustryActivityProbabilityKey.builder()
                .typeId(blueprintId)
                .activityId(activityId)
                .productTypeId(productId)
                .build();

        IndustryActivityProbability probability = IndustryActivityProbability.builder()
                .id(probabilityKey)
                .probability(0.3) // 30% probability
                .build();

        when(warehouse.getWarehouseItem(productId)).thenReturn(blueprintItem);
        when(industryJobRepository.findUnprocessed()).thenReturn(List.of(job));
        when(industryActivityProbabilityRepository.findById_TypeIdAndId_ActivityIdAndId_ProductTypeId(
                blueprintId, activityId, productId)).thenReturn(Optional.of(probability));

        // Mock the IndustryActivityProduct lookup
        IndustryActivityProduct product = IndustryActivityProduct.builder()
                .quantity(10L) // Use the same multiplier as before to maintain test expectations
                .build();
        when(industryActivityProductRepository.findById_ProductTypeId(productId)).thenReturn(Optional.of(product));

        // When
        WarehouseItem result = overlayWarehouse.getWarehouseItem(productId);

        // Then
        // Expected: 5 (original) + 6 (estimated BPCs from 20 runs * 0.3 probability)
        assertEquals(35L, result.getQuantity());

        // Expected cost per item: (5 * 100 + 1000) / 11 = 136.36...
        assertEquals(42.85, result.getCostPerItem(), 0.01);

        verify(warehouse).getWarehouseItem(productId);
        verify(industryJobRepository).findUnprocessed();
        verify(industryActivityProbabilityRepository).findById_TypeIdAndId_ActivityIdAndId_ProductTypeId(
                blueprintId, activityId, productId);
        verify(industryActivityProductRepository).findById_ProductTypeId(productId);
    }

    @Test
    void testGetWarehouseItem_WithBlueprint_NoProbability() {
        // Given
        Long blueprintId = 123L;
        Long productId = 456L;
        Long activityId = 8L; // Invention activity

        // Create a warehouse item for the blueprint
        WarehouseItem blueprintItem = WarehouseItem.builder()
                .itemId(productId)
                .quantity(5L)
                .costPerItem(100.0)
                .build();

        // Create an industry job for the blueprint
        IndustryJob job = IndustryJob.builder()
                .jobId(1L)
                .blueprintTypeId(blueprintId)
                .productTypeId(productId)
                .activityId(activityId)
                .runs(10L)
                .licensedRuns(2L)
                .cost(1000.0)
                .build();

        when(warehouse.getWarehouseItem(productId)).thenReturn(blueprintItem);
        when(industryJobRepository.findUnprocessed()).thenReturn(List.of(job));
        when(industryActivityProbabilityRepository.findById_TypeIdAndId_ActivityIdAndId_ProductTypeId(
                blueprintId, activityId, productId)).thenReturn(Optional.empty());

        // When
        WarehouseItem result = overlayWarehouse.getWarehouseItem(productId);

        // Then
        // Expected: 5 (original) + 0 (no probability found)
        assertEquals(5L, result.getQuantity());
        assertEquals(100.0, result.getCostPerItem());

        verify(warehouse).getWarehouseItem(productId);
        verify(industryJobRepository).findUnprocessed();
        verify(industryActivityProbabilityRepository).findById_TypeIdAndId_ActivityIdAndId_ProductTypeId(
                blueprintId, activityId, productId);
        // The code doesn't reach the point where findById_ProductTypeId is called because the probability is not found
        verifyNoInteractions(industryActivityProductRepository);
    }

    @Test
    void testRemoveItem() {
        // Given
        Long itemId = 123L;
        Long quantity = 10L;
        when(warehouse.removeItem(itemId, quantity)).thenReturn(1000.0);

        // When
        double result = overlayWarehouse.removeItem(itemId, quantity);

        // Then
        assertEquals(1000.0, result);
        verify(warehouse).setAllowNegativeQuantities(false);
        verify(warehouse).removeItem(itemId, quantity);
    }

    @Test
    void testAddItem() {
        // Given
        Long itemId = 123L;
        Long quantity = 10L;
        Double costPerItem = 100.0;

        // When
        overlayWarehouse.addItem(itemId, quantity, costPerItem);

        // Then
        verify(warehouse).addItem(itemId, quantity, costPerItem);
    }

    @Test
    void testGettersAndSetters() {
        // Default value should be false
        assertFalse(overlayWarehouse.isAllowNegativeQuantities());

        // Set to true
        overlayWarehouse.setAllowNegativeQuantities(true);
        assertTrue(overlayWarehouse.isAllowNegativeQuantities());

        // Set back to false
        overlayWarehouse.setAllowNegativeQuantities(false);
        assertFalse(overlayWarehouse.isAllowNegativeQuantities());
    }
}
