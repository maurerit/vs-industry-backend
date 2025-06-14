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

package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.ExtraCostDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ProductToIgnoreDTO;
import io.github.vaporsea.vsindustry.service.ExtraCostService;
import io.github.vaporsea.vsindustry.service.TypeService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/config")
@RolesAllowed("ADMIN")
public class ConfigurationsController {
    
    private final ExtraCostService extraCostService;
    private final TypeService typeService;
    
    @GetMapping("/extracost")
    public Page<ExtraCostDTO> getExtraCosts(int page, int pageSize) {
        return extraCostService.getExtraCosts(page, pageSize);
    }
    
    @PostMapping("/extracost")
    public ExtraCostDTO saveExtraCost(@RequestBody ExtraCostDTO extraCostDTO) {
        return extraCostService.save(extraCostDTO);
    }
    
    @GetMapping("/extracost/{itemId}")
    public ExtraCostDTO getExtraCost(@PathVariable long itemId) {
        return extraCostService.getExtraCost(itemId);
    }
    
    @GetMapping("/ignoredproduct")
    public Page<ProductToIgnoreDTO> getIgnoredProducts(int page, int pageSize) {
        return typeService.getIgnoredProducts(page, pageSize);
    }
    
    @PostMapping("/ignoredproduct")
    public ResponseEntity<Void> saveIgnoredProduct(@RequestBody ProductToIgnoreDTO ptiDto) {
        typeService.save(ptiDto);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/ignoredproduct/{productId}")
    public ResponseEntity<Void> deleteIgnoredProduct(@PathVariable long productId) {
        typeService.delete(ProductToIgnoreDTO.builder().productId(productId).build());
        return ResponseEntity.ok().build();
    }
}
