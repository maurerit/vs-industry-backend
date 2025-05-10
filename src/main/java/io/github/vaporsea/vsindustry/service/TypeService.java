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

import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ItemDTO;
import io.github.vaporsea.vsindustry.contract.ProductToIgnoreDTO;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import io.github.vaporsea.vsindustry.domain.ProductToIgnore;
import io.github.vaporsea.vsindustry.domain.ProductToIgnoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TypeService {
    
    private final ItemRepository typeRepository;
    private final ProductToIgnoreRepository productToIgnoreRepository;
    
    public Page<ItemDTO> getTypes(int page, int pageSize, MarketGroupTypeSearch search) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        //This returns a spring page, we're transforming to our own page so use var
        var typePage = typeRepository.findAll(search, pageRequest);
        
        List<ItemDTO> itemDtos =
                typePage.map(type -> new ItemDTO(type.getItemId(), type.getName(), type.getDescription()))
                        .toList();
        
        return new Page<>(
                typePage.getNumber(),
                typePage.getTotalPages(),
                typePage.getTotalElements(),
                itemDtos
        );
    }
    
    public ItemDTO getTypeById(Long id) {
        return typeRepository.findById(id)
                             .map(type -> new ItemDTO(type.getItemId(), type.getName(), type.getDescription()))
                             .orElse(null);
    }
    
    public Page<ProductToIgnoreDTO> getIgnoredProducts(int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        var typePage = productToIgnoreRepository.findAll(pageRequest);
        
        List<ProductToIgnoreDTO> ptiDtos =
                typePage.map(pti -> {
                            var type = typeRepository.findById(pti.getProductId()).orElse(new Item());
                            return ProductToIgnoreDTO.builder()
                                                     .productId(pti.getProductId())
                                                     .productName(type.getName())
                                                     .reason(pti.getReason())
                                                     .build();
                        })
                        .toList();
        
        return new Page<>(
                typePage.getNumber(),
                typePage.getTotalPages(),
                typePage.getTotalElements(),
                ptiDtos
        );
    }
    
    public void save(ProductToIgnoreDTO productToIgnoreDTO) {
        var productToIgnore = productToIgnoreRepository.findById(productToIgnoreDTO.getProductId())
                                                       .orElse(new ProductToIgnore());
        productToIgnore.setProductId(productToIgnoreDTO.getProductId());
        productToIgnore.setReason(productToIgnoreDTO.getReason());
        productToIgnoreRepository.save(productToIgnore);
    }
    
    public void delete(ProductToIgnoreDTO ptiDto) {
        var productToIgnore = productToIgnoreRepository.findById(ptiDto.getProductId());
        productToIgnore.ifPresent(productToIgnoreRepository::delete);
    }
}
