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
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.domain.ExtraCost;
import io.github.vaporsea.vsindustry.domain.ExtraCostRepository;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import io.github.vaporsea.vsindustry.domain.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ExtraCostService {

    private final ExtraCostRepository extraCostRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;

    /**
     * Get a specific extra cost by item ID and cost type
     *
     * @param itemId the item ID
     * @param costType the cost type, defaults to "default" if not specified
     *
     * @return the extra cost DTO
     */
    public ExtraCostDTO getExtraCost(Long itemId, String costType) {
        if (costType == null || costType.isEmpty()) {
            costType = "default";
        }

        String finalizedCostType = costType;
        ExtraCost extraCost = extraCostRepository.findByItemIdAndCostType(itemId, costType)
                                                 .orElseThrow(
                                                         () -> new IllegalArgumentException(
                                                                 "Extra cost not found for item " + itemId +
                                                                         " and cost type " + finalizedCostType));
        return mapExtraCost(extraCost);
    }

    /**
     * Get an extra cost by item ID using the default cost type This maintains backward compatibility with existing
     * code
     *
     * @param itemId the item ID
     *
     * @return the extra cost DTO
     */
    public ExtraCostDTO getExtraCost(Long itemId) {
        return getExtraCost(itemId, "default");
    }

    /**
     * Get paginated list of extra costs
     *
     * @param page the page number
     * @param pageSize the page size
     *
     * @return a page of extra cost DTOs
     */
    public Page<ExtraCostDTO> getExtraCosts(int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        var extraCostPage = extraCostRepository.findAll(pageRequest);

        return new Page<>(
                extraCostPage.getNumber(),
                extraCostPage.getTotalPages(),
                extraCostPage.getTotalElements(),
                extraCostPage.getContent().stream()
                             .map(this::mapExtraCost)
                             .toList()
        );
    }

    /**
     * Save an extra cost If cost type is not specified, 'default' will be used
     *
     * @param extraCostDTO the extra cost DTO to save
     *
     * @return the saved extra cost DTO
     */
    public ExtraCostDTO save(ExtraCostDTO extraCostDTO) {
        // Ensure costType is set for backward compatibility
        if (extraCostDTO.getCostType() == null || extraCostDTO.getCostType().isEmpty()) {
            extraCostDTO.setCostType("default");
        }

        ExtraCost extraCost = modelMapper.map(extraCostDTO, ExtraCost.class);
        extraCost = extraCostRepository.save(extraCost);
        return mapExtraCost(extraCost);
    }

    /**
     * Calculate the extra cost for a product
     *
     * @param productItem the product item
     * @return the total extra cost for the product
     */
    public double extraCost(Product productItem) {
        List<ExtraCost> extraCosts = extraCostRepository.findByItemId(productItem.getItemId());
        if (extraCosts.isEmpty()) {
            return 0.0;
        }

        return extraCosts.stream()
                         .mapToDouble(ExtraCost::getCost)
                         .sum();
    }

    /**
     * Map an ExtraCost entity to an ExtraCostDTO
     *
     * @param extraCost the extra cost entity
     *
     * @return the mapped extra cost DTO
     */
    private ExtraCostDTO mapExtraCost(ExtraCost extraCost) {
        ExtraCostDTO extraCostDto = modelMapper.map(extraCost, ExtraCostDTO.class);
        extraCostDto.setName(itemRepository.findById(extraCost.getItemId()).orElse(new Item()).getName());
        return extraCostDto;
    }
}
