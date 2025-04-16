package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.ExtraCostDTO;
import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.domain.ExtraCost;
import io.github.vaporsea.vsindustry.domain.ExtraCostRepository;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExtraCostService {
    
    private final ExtraCostRepository extraCostRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;
    
    public ExtraCostDTO getExtraCost(Long itemId) {
        ExtraCost extraCost = extraCostRepository.findById(itemId)
                                                 .orElseThrow(
                                                         () -> new IllegalArgumentException("Extra cost not found"));
        return mapExtraCost(extraCost);
    }
    
    public Page<ExtraCostDTO> getExtraCosts(int page, int pageSize) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        var extraCostPage = extraCostRepository.findAll(pageRequest);
        
        return new Page<>(
                extraCostPage.getNumber(),
                extraCostPage.getTotalPages(),
                extraCostPage.getTotalElements(),
                extraCostPage.getContent().stream()
                             .map(this::mapExtraCost)
                             .toList()
        );
    }
    
    public ExtraCostDTO save(ExtraCostDTO extraCostDTO) {
        ExtraCost extraCost = modelMapper.map(extraCostDTO, ExtraCost.class);
        extraCost = extraCostRepository.save(extraCost);
        return mapExtraCost(extraCost);
    }
    
    private ExtraCostDTO mapExtraCost(ExtraCost extraCost) {
        ExtraCostDTO extraCostDto = modelMapper.map(extraCost, ExtraCostDTO.class);
        extraCostDto.setName(itemRepository.findById(extraCost.getItemId()).orElse(new Item()).getName());
        return extraCostDto;
    }
}
