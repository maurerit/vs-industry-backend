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
