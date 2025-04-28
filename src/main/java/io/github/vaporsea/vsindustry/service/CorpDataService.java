package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.IndustryJobDTO;
import io.github.vaporsea.vsindustry.domain.IndustryJob;
import io.github.vaporsea.vsindustry.domain.IndustryJobRepository;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CorpDataService {
    
    private final IndustryJobRepository industryJobRepository;
    private final ItemRepository itemRepository;
    private final ModelMapper modelMapper;
    
    public Page<IndustryJobDTO> getIndustryJobs(IndustryJobStatusSearch search, Pageable pageable) {
        var data = industryJobRepository.findAll(search, pageable);
        
        return data.map(this::mapIndustryJob);
    }
    
    private IndustryJobDTO mapIndustryJob(IndustryJob industryJob) {
        IndustryJobDTO industryJobMapped = modelMapper.map(industryJob, IndustryJobDTO.class);
        
        industryJobMapped.setItemName(
                itemRepository.findById(industryJob.getProductTypeId()).orElse(new Item()).getName());
        
        return industryJobMapped;
    }
}
