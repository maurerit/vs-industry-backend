package io.github.vaporsea.vsindustry.util;

import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.Product;
import io.github.vaporsea.vsindustry.domain.ProductItem;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaterialCostTest {
    @Test
    void testCalculate_when3EachAnd10RunsAtME2_thenEquals30() {
        // Given
        var productItems = List.of(
                ProductItem.builder().item(Item.builder().build()).product(Product.builder().build()).quantity(3L).build()
        );
        int meLevel = 2;
        int runs = 10;

        // When
        var result = MaterialCost.calculate(productItems, meLevel, runs);

        // Then
        assertEquals(1, result.size());
        assertEquals(30L, result.get(0).getQuantity());
    }
    
    @Test
    void testCalculate_when5EachAnd10RunsAtME2_thenEquals49() {
        // Given
        var productItems = List.of(
                ProductItem.builder().item(Item.builder().build()).product(Product.builder().build()).quantity(5L).build()
        );
        int meLevel = 2;
        int runs = 10;

        // When
        var result = MaterialCost.calculate(productItems, meLevel, runs);

        // Then
        assertEquals(1, result.size());
        assertEquals(49L, result.get(0).getQuantity());
    }
}