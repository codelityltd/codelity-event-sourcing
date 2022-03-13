package uk.co.codelity.inventory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.common.Metadata;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateLoadException;
import uk.co.codelity.event.sourcing.core.service.AggregateService;
import uk.co.codelity.inventory.aggregate.ProductStock;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {
    @Mock
    private EventStore eventStore;

    @Mock
    private AggregateService aggregateService;

    @Mock
    private ProductStock productStock;

    @InjectMocks
    private StockService stockService;

    @Test
    void shouldAppendStockIncreasedEvent() throws AggregateLoadException, EventPersistenceException {
        UUID productId = UUID.randomUUID();
        Metadata metadata = new Metadata(UUID.randomUUID(), "UserId");
        Envelope<?> event = new Envelope<>(metadata, new StockIncreased(productId, 1));
        when(aggregateService.load(productId.toString(), ProductStock.class)).thenReturn(productStock);
        when(productStock.supply(productId, 1, metadata)).thenReturn(List.of(event));

        stockService.supply(productId, 1, metadata);

        verify(eventStore, times(1)).append(productId.toString(), List.of(event));
    }

    @Test
    void shouldAppendStockDecreasedEvent() throws AggregateLoadException, EventPersistenceException {
        UUID productId = UUID.randomUUID();
        Metadata metadata = new Metadata(UUID.randomUUID(), "UserId");
        Envelope<?> event = new Envelope<>(metadata, new StockDecreased(productId, 1));
        when(aggregateService.load(productId.toString(), ProductStock.class)).thenReturn(productStock);
        when(productStock.dispatch(productId, 1, metadata)).thenReturn(List.of(event));

        stockService.dispatch(productId, 1, metadata);

        verify(eventStore, times(1)).append(productId.toString(), List.of(event));
    }
}