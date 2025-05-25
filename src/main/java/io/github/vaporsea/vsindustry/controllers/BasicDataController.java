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

import io.github.vaporsea.vsindustry.contract.IdLabelValue;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ItemDTO;
import io.github.vaporsea.vsindustry.contract.fuzzwork.BlueprintDTO;
import io.github.vaporsea.vsindustry.service.BlueprintService;
import io.github.vaporsea.vsindustry.service.MarketGroupTypeSearch;
import io.github.vaporsea.vsindustry.service.TypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/data")
public class BasicDataController {
    
    private final TypeService typeService;
    private final BlueprintService blueprintService;
    
    @GetMapping("/type")
    public Page<ItemDTO> getTypes(int page, int pageSize, MarketGroupTypeSearch search) {
        return typeService.getTypes(page, pageSize, search);
    }
    
    @GetMapping("/type/{id}")
    public ItemDTO getTypeById(@PathVariable Long id) {
        return typeService.getTypeById(id);
    }
    
    @GetMapping("/blueprintName/{search}")
    public List<IdLabelValue> getNamesForTypesThatHaveBlueprints(@PathVariable String search) {
        return blueprintService.getNamesForTypesThatHaveBlueprints(search);
    }
    
    @GetMapping("/blueprint/{itemId}")
    public BlueprintDTO getBlueprintDetails(@PathVariable Long itemId) {
        return blueprintService.getBlueprintDetails(itemId);
    }
}
