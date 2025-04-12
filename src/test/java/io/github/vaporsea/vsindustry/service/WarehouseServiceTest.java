package io.github.vaporsea.vsindustry.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.vaporsea.vsindustry.domain.ItemRepository;
import io.github.vaporsea.vsindustry.domain.LastProcessedRepository;
import io.github.vaporsea.vsindustry.domain.ProductRepository;
import io.github.vaporsea.vsindustry.domain.WarehouseItem;
import io.github.vaporsea.vsindustry.domain.WarehouseItemRepository;

/**
 * @author Matt Maurer <br>
 * @since 6/10/2024
 */
@ExtendWith(MockitoExtension.class)
class WarehouseServiceTest {
    @InjectMocks
    private WarehouseService warehouseService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private WarehouseItemRepository warehouseItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private LastProcessedRepository lastProcessedRepository;

    @Captor
    private ArgumentCaptor<WarehouseItem> warehouseItemCaptor;
}
