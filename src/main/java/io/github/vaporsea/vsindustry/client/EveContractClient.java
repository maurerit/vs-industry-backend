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

import io.github.vaporsea.vsindustry.contract.ContractDetailDTO;
import io.github.vaporsea.vsindustry.contract.ContractHeaderDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EveContractClient {
    
    private static final String CONTRACT_HEADER_URL = "/v1/corporations/{corporation_id}/contracts/";
    private static final String CONTRACT_DETAIL_URL =
            "/v1/corporations/{corporation_id}/contracts/{contract_id}/items/";
    
    private final RestClient restClient;
    
    /**
     * Get a list of contracts for a corporation.
     *
     * @param corporationId The ID of the corporation.
     * @param page The page number to retrieve.
     *
     * @return A page of contract headers.
     */
    @Cacheable(value = "contractHeaders", key = "#corporationId + #page")
    @Retryable
    public Page<ContractHeaderDTO> getContracts(String corporationId, String page) {
        ResponseEntity<List<ContractHeaderDTO>> contracts = restClient.get()
                                                                      .uri(uriBuilder -> uriBuilder.path(
                                                                                                           CONTRACT_HEADER_URL)
                                                                                                   .queryParam("page",
                                                                                                           page)
                                                                                                   .build(corporationId))
                                                                      .retrieve()
                                                                      .toEntity(new ParameterizedTypeReference<>() {
                                                                      });
        
        return new Page<>(Integer.parseInt(page), Integer.parseInt(contracts.getHeaders().get("x-pages").get(0)),
                0, contracts.getBody());
    }
    
    /**
     * Get the details of a specific contract.
     *
     * @param corporationId The ID of the corporation.
     * @param contractId The ID of the contract.
     *
     * @return A list of contract details.
     */
    @Cacheable(value = "contractDetails", key = "#corporationId + #contractId")
    @Retryable
    public List<ContractDetailDTO> getContractDetails(String corporationId, String contractId) {
        return restClient.get()
                         .uri(uriBuilder -> uriBuilder.path(CONTRACT_DETAIL_URL).build(corporationId, contractId))
                         .retrieve()
                         .body(
                                 new ParameterizedTypeReference<>() {
                                 });
    }
}
