package io.github.vaporsea.vsindustry.util;

import io.github.vaporsea.vsindustry.domain.ProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for calculating material costs with Material Efficiency (ME) adjustments.
 */
public class MaterialCost {

    /**
     * Calculates the material requirements using EVE Online's ME calculation.
     * Formula: Required Materials = Base Materials * (1 - (ME Level / 100))
     *
     * @param productItems List of ProductItem representing base material requirements
     * @param meLevel Material Efficiency level (integer)
     * @return List of ProductItem with quantities adjusted based on ME level
     */
    public static List<ProductItem> calculate(List<ProductItem> productItems, int meLevel, int runs) {
        if (productItems == null || productItems.isEmpty()) {
            return new ArrayList<>();
        }

        List<ProductItem> adjustedItems = new ArrayList<>();

        for (ProductItem originalItem : productItems) {
            // Create a copy of the original item
            ProductItem adjustedItem = ProductItem.builder()
                    .product(originalItem.getProduct())
                    .item(originalItem.getItem())
                    .build();

            // Calculate adjusted quantity using ME formula
            long baseQuantity = originalItem.getQuantity() * runs;
            long adjustedQuantity = Math.max(1, Math.round(baseQuantity * (1 - (meLevel / 100.0))));
            adjustedItem.setQuantity(adjustedQuantity);

            adjustedItems.add(adjustedItem);
        }

        return adjustedItems;
    }
}
