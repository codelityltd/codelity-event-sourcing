package uk.co.codelity.inventory.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.Metadata;
import uk.co.codelity.inventory.entity.Inventory;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;
import uk.co.codelity.inventory.repository.InventoryRepository;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockEventHandlerTest {
    @Mock
    private InventoryRepository inventoryRepository;

    @Captor
    private ArgumentCaptor<Inventory> inventoryArgumentCaptor;

    @InjectMocks
    private StockEventHandler stockEventHandler;

    @Test
    void shouldStockIncreasedForNewRecord() {
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());
        Envelope<StockIncreased> envelope = new Envelope<>(new Metadata(), new StockIncreased(productId, 2));
        stockEventHandler.stockIncreased(envelope);
        verify(inventoryRepository, times(1)).save(inventoryArgumentCaptor.capture());
        assertThat(inventoryArgumentCaptor.getValue().getProductId(), is(productId));
        assertThat(inventoryArgumentCaptor.getValue().getStock(), is(2));
    }

    @Test
    void shouldStockIncreasedForExistingRecord() {
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(new Inventory(productId, 1)));
        Envelope<StockIncreased> envelope = new Envelope<>(new Metadata(), new StockIncreased(productId, 2));
        stockEventHandler.stockIncreased(envelope);
        verify(inventoryRepository, times(1)).save(inventoryArgumentCaptor.capture());
        assertThat(inventoryArgumentCaptor.getValue().getProductId(), is(productId));
        assertThat(inventoryArgumentCaptor.getValue().getStock(), is(3));
    }

    @Test
    void shouldNeverCallSaveWhenNoStockFound() {
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());
        StockDecreased stockDecreased = new StockDecreased(productId, 2);
        stockEventHandler.stockDecreased(stockDecreased);
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldNeverCallSaveWhenStockNotEnough() {
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(new Inventory(productId, 1)));
        StockDecreased stockDecreased = new StockDecreased(productId, 2);
        stockEventHandler.stockDecreased(stockDecreased);
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    void shouldStockDecreasedWhenStockFound() {
        UUID productId = UUID.randomUUID();
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(new Inventory(productId, 2)));
        StockDecreased stockDecreased = new StockDecreased(productId, 2);
        stockEventHandler.stockDecreased(stockDecreased);
        verify(inventoryRepository, times(1)).save(inventoryArgumentCaptor.capture());
        assertThat(inventoryArgumentCaptor.getValue().getProductId(), is(productId));
        assertThat(inventoryArgumentCaptor.getValue().getStock(), is(0));
    }
}