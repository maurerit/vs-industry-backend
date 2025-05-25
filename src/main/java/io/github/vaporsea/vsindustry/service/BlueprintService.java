package io.github.vaporsea.vsindustry.service;

import io.github.vaporsea.vsindustry.contract.IdLabelValue;
import io.github.vaporsea.vsindustry.contract.fuzzwork.BlueprintDTO;
import io.github.vaporsea.vsindustry.contract.fuzzwork.BlueprintDetailsDTO;
import io.github.vaporsea.vsindustry.contract.fuzzwork.MaterialDTO;
import io.github.vaporsea.vsindustry.domain.IndustryActivityMaterial;
import io.github.vaporsea.vsindustry.domain.IndustryActivityMaterialRepository;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProduct;
import io.github.vaporsea.vsindustry.domain.IndustryActivityProductRepository;
import io.github.vaporsea.vsindustry.domain.Item;
import io.github.vaporsea.vsindustry.domain.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class BlueprintService {
    
    private static final Map<String, Integer> TECH_LEVEL_MAP = Map.of(
            "Tech I", 1,
            "Tech II", 2,
            "Tech III", 3
    );
    
    private final ItemRepository itemRepository;
    private final IndustryActivityProductRepository iapRepository;
    private final IndustryActivityMaterialRepository iamRepository;
    
    public List<IdLabelValue> getNamesForTypesThatHaveBlueprints(String search) {
        return itemRepository.findByNameWithBlueprint("%" + search + "%")
                             .stream()
                             .map(item -> IdLabelValue.builder()
                                                      .id(item.getItemId().toString())
                                                      .label(item.getName())
                                                      .value(search)
                                                      .build())
                             .toList();
    }
    
    public BlueprintDTO getBlueprintDetails(Long itemId) {
        IndustryActivityProduct activityProduct = iapRepository.findById_ProductTypeId(itemId)
                                                               .orElseThrow(() -> new EntityNotFoundException(
                                                                       "IndustryActivityProduct not found"));
        Item item = itemRepository.findById(itemId).orElseThrow();
        
        Long blueprintId = activityProduct.getId().getTypeId();
        List<IndustryActivityMaterial> productionMaterials =
                iamRepository.findById_TypeIdAndId_ActivityId(blueprintId, 1L);
        
        List<IndustryActivityMaterial> copyMaterials = iamRepository.findById_TypeIdAndId_ActivityId(blueprintId, 5L);
        
        List<IndustryActivityMaterial> inventionMaterials =
                iamRepository.findById_TypeIdAndId_ActivityId(blueprintId, 8L);
        
        return BlueprintDTO.builder()
                           .requestedid(itemId.intValue())
                           .blueprintDetails(BlueprintDetailsDTO.builder()
                                                                .productTypeID(itemId)
                                                                .productTypeName(item.getName())
                                                                .productQuantity(activityProduct.getQuantity())
                                                                .techLevel(TECH_LEVEL_MAP.get(item.getMetaType()
                                                                                                  .getMetaGroup()
                                                                                                  .getMetaGroupName()))
                                                                .build())
                           .activityMaterials(
                                   mapActivityMaterials(productionMaterials, copyMaterials, inventionMaterials))
                           .build();
    }
    
    private Map<String, List<MaterialDTO>> mapActivityMaterials(List<IndustryActivityMaterial> productionMaterials,
            List<IndustryActivityMaterial> copyMaterials, List<IndustryActivityMaterial> inventionMaterials) {
        return Map.of(
                "1", productionMaterials.stream().map(mapMaterial()).toList(),
                "5", copyMaterials.stream().map(mapMaterial()).toList(),
                "8", inventionMaterials.stream().map(mapMaterial()).toList()
        );
    }
    
    private Function<? super IndustryActivityMaterial, MaterialDTO> mapMaterial() {
        return material -> MaterialDTO.builder()
                                      .typeid(material.getId().getMaterialTypeId())
                                      .name(itemRepository.findById(material.getId().getMaterialTypeId())
                                                          .orElse(new Item())
                                                          .getName())
                                      .quantity(material.getQuantity())
                                      .build();
    }
}
