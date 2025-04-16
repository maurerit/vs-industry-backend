package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.Page;
import io.github.vaporsea.vsindustry.contract.ItemDTO;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TypeService {
    
    private final ItemRepository typeRepository;
    
    public Page<ItemDTO> getTypes(int page, int pageSize, MarketGroupTypeSearch search) {
        Pageable pageRequest = PageRequest.of(page, pageSize);
        //This returns a spring page, we're transforming to our own page so use var
        var typePage = typeRepository.findAll(search, pageRequest);
        
        List<ItemDTO> itemDtos =
                typePage.map(type -> new ItemDTO(type.getItemId(), type.getName(), type.getDescription()))
                        .toList();
        
        return new Page<>(
                typePage.getNumber(),
                typePage.getTotalPages(),
                typePage.getTotalElements(),
                itemDtos
        );
    }
    
    public ItemDTO getTypeById(Long id) {
        return typeRepository.findById(id)
                             .map(type -> new ItemDTO(type.getItemId(), type.getName(), type.getDescription()))
                             .orElse(null);
    }
}
