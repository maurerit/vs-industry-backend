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

    /**
     * Adds an item to the warehouse as if it were purchased.
     * This is different from the save endpoint as it uses the warehouse component's addItem method
     * which handles the rolling average calculation for the cost per item.
     *
     * @param warehouseItemDTO The warehouse item to add
     */
    @PostMapping("/add-item")
    public void addItem(@RequestBody WarehouseItemDTO warehouseItemDTO) {
        warehouseService.addItem(warehouseItemDTO);
    }
}
