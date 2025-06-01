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

package io.github.vaporsea.vsindustry.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.github.vaporsea.vsindustry.contract.MarketOrderDTO;
import io.github.vaporsea.vsindustry.util.TradeHub;
import lombok.RequiredArgsConstructor;

/**
 * Client for interacting with the EVE Online market API.
 *
 * @author Matt Maurer <br>
 * @since 6/9/2024
 */
@RequiredArgsConstructor
@Component
public class MarketClient {

    private static final String MARKET_ORDERS_URL = "/v1/markets/{region_id}/orders/";

    private final RestClient restClient;

    /**
     * Fetch market orders for a specific type ID and filter by system ID.
     *
     * @param typeId The type ID to fetch orders for
     * @param systemId The system ID to filter orders by
     * @param regionId The region ID to fetch orders from
     * @param orderType The type of orders to fetch ("buy", "sell", or "all")
     * @return A list of market orders for the specified type in the specified system
     */
    @Cacheable(value = "marketOrders", key = "#typeId + '-' + #systemId + '-' + #regionId + '-' + #orderType")
    @Retryable
    public List<MarketOrderDTO> getOrders(Long typeId, Long systemId, Long regionId, String orderType) {
        List<MarketOrderDTO> allOrders = new ArrayList<>();

        // Get the first page of results
        ResponseEntity<List<MarketOrderDTO>> response = getOrdersResponse(typeId, regionId, orderType, 1);

        if (response.getBody() != null) {
            allOrders.addAll(response.getBody());
        }

        // Check if there are more pages
        String pagesHeader = response.getHeaders().getFirst("x-pages");
        if (pagesHeader != null) {
            int totalPages = Integer.parseInt(pagesHeader);

            // Fetch remaining pages
            for (int page = 2; page <= totalPages; page++) {
                ResponseEntity<List<MarketOrderDTO>> pageResponse = getOrdersResponse(typeId, regionId, orderType, page);

                if (pageResponse.getBody() != null) {
                    allOrders.addAll(pageResponse.getBody());
                }
            }
        }

        // Filter orders by system ID
        return allOrders.stream()
                .filter(order -> Objects.equals(order.getSystemId(), systemId))
                .collect(Collectors.toList());
    }
    
    private ResponseEntity<List<MarketOrderDTO>> getOrdersResponse(Long typeId, Long regionId, String orderType, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder.path(MARKET_ORDERS_URL)
                        .queryParam("type_id", typeId)
                        .queryParam("order_type", orderType)
                        .queryParam("page", page)
                        .build(regionId))
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});
    }

    /**
     * Fetch market orders for a specific type ID in a trade hub.
     * This is a convenience method that uses the region and system IDs from the TradeHub enum.
     *
     * @param typeId The type ID to fetch orders for
     * @param tradeHub The trade hub to fetch orders from
     * @param orderType The type of orders to fetch ("buy", "sell", or "all")
     * @return A list of market orders for the specified type in the specified trade hub
     */
    @Cacheable(value = "marketOrders", key = "#typeId + '-' + #tradeHub.name() + '-' + #orderType")
    @Retryable
    public List<MarketOrderDTO> getOrders(Long typeId, TradeHub tradeHub, String orderType) {
        return getOrders(typeId, tradeHub.getSystemId(), tradeHub.getRegionId(), orderType);
    }

    /**
     * Fetch market orders for a specific type ID in a trade hub.
     * This is a convenience method that uses the region and system IDs from the TradeHub enum.
     * Defaults to fetching all order types.
     *
     * @param typeId The type ID to fetch orders for
     * @param tradeHub The trade hub to fetch orders from
     * @return A list of market orders for the specified type in the specified trade hub
     */
    @Cacheable(value = "marketOrders", key = "#typeId + '-' + #tradeHub.name() + '-all'")
    @Retryable
    public List<MarketOrderDTO> getOrders(Long typeId, TradeHub tradeHub) {
        return getOrders(typeId, tradeHub.getSystemId(), tradeHub.getRegionId(), "all");
    }
}
