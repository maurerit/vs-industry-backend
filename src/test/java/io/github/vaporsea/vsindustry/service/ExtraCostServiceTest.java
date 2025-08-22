/*
 * MIT License
 *
 * Copyright (c) 2025 VaporSea
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.ExtraCostDTO;
import io.github.vaporsea.vsindustry.domain.ExtraCost;
import io.github.vaporsea.vsindustry.domain.ExtraCostRepository;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExtraCostServiceTest {

    @Mock
    private ExtraCostRepository extraCostRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ExtraCostService extraCostService;

    private ExtraCostDTO extraCostDTO;
    private ExtraCost extraCost;
    private Item item;

    @BeforeEach
    void setUp() {
        // Setup test data
        extraCostDTO = new ExtraCostDTO();
        extraCostDTO.setItemId(1L);
        extraCostDTO.setCostType("default");
        extraCostDTO.setCost(100.0);
        // originalCostType is intentionally left null to test our fix

        extraCost = new ExtraCost();
        extraCost.setItemId(1L);
        extraCost.setCostType("default");
        extraCost.setCost(100.0);

        item = new Item();
        item.setItemId(1L);
        item.setName("Test Item");
    }

    @Test
    void testSaveWithNullOriginalCostType() {
        // Setup mocks
        when(modelMapper.map(any(ExtraCostDTO.class), any())).thenReturn(extraCost);
        when(extraCostRepository.save(any(ExtraCost.class))).thenReturn(extraCost);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(modelMapper.map(any(ExtraCost.class), any())).thenReturn(extraCostDTO);

        // Execute the method
        ExtraCostDTO result = extraCostService.save(extraCostDTO);

        // Verify the result
        assertNotNull(result);
        assertEquals(1L, result.getItemId());
        assertEquals("default", result.getCostType());
        assertEquals(100.0, result.getCost());
    }
}