package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.BlueprintDataDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ProductDTO;
import io.github.vaporsea.vsindustry.service.ProductFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
public class ProductController {
    
    private final ProductFetchService productFetchService;
    
    @GetMapping
    public Page<ProductDTO> getProducts(Integer page, Integer pageSize) {
        return productFetchService.getProducts(PageRequest.of(page, pageSize));
    }
    
    @GetMapping("/{itemId}")
    public BlueprintDataDTO getProduct(@PathVariable Long itemId) {
        return productFetchService.getProduct(itemId);
    }
}
