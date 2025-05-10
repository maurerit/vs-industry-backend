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
