package io.github.vaporsea.vsindustry.client;

import io.github.vaporsea.vsindustry.contract.ContractDetailDTO;
import io.github.vaporsea.vsindustry.contract.ContractHeaderDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
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
                contracts.getBody());
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
    public List<ContractDetailDTO> getContractDetails(String corporationId, String contractId) {
        return restClient.get()
                         .uri(uriBuilder -> uriBuilder.path(CONTRACT_DETAIL_URL).build(corporationId, contractId))
                         .retrieve()
                         .body(
                                 new ParameterizedTypeReference<>() {
                                 });
    }
}
