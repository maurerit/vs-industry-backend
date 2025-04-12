package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.WarehouseItemDTO;
import io.github.vaporsea.vsindustry.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    
    @GetMapping
    public List<WarehouseItemDTO> getWarehouse() {
        return warehouseService.getWarehouse();
    }
    
    @PostMapping("/processAll")
    public void processAll() {
        warehouseService.processAll();
    }
    
    @PostMapping
    public void save(@RequestBody WarehouseItemDTO warehouseItemDTO) {
        warehouseService.save(warehouseItemDTO);
    }
}
