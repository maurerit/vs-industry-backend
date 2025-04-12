package io.github.vaporsea.vsindustry.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.vaporsea.vsindustry.contract.fuzzwork.BlueprintDTO;
import io.github.vaporsea.vsindustry.service.ProductSetupService;
import lombok.RequiredArgsConstructor;

/**
 * @author Matt Maurer <br>
 * @since 6/17/2024
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/product-setup")
public class ProductSetupController {

    private final ProductSetupService productSetupService;

    @PostMapping
    public ResponseEntity<Void> setupProduct(@RequestBody BlueprintDTO blueprintDTO) {
        productSetupService.setupProduct(blueprintDTO);
        return ResponseEntity.ok().build();
    }
}
