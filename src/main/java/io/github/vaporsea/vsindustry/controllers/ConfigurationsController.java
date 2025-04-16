package io.github.vaporsea.vsindustry.controllers;

import io.github.vaporsea.vsindustry.contract.ExtraCostDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.service.ExtraCostService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/config")
@RolesAllowed("ROLE_ADMIN")
public class ConfigurationsController {
    
    private final ExtraCostService extraCostService;
    
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
}
