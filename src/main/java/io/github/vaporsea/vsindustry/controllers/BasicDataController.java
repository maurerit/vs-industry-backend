package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ItemDTO;
import io.github.vaporsea.vsindustry.service.MarketGroupTypeSearch;
import io.github.vaporsea.vsindustry.service.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/data")
public class BasicDataController {
    
    private final TypeService typeService;
    
    @GetMapping("/type")
    public Page<ItemDTO> getTypes(int page, int pageSize, MarketGroupTypeSearch search) {
        return typeService.getTypes(page, pageSize, search);
    }
    
    @GetMapping("/type/{id}")
    public ItemDTO getTypeById(@PathVariable Long id) {
        return typeService.getTypeById(id);
    }
}
