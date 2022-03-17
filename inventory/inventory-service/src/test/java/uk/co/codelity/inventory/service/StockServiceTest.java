package uk.co.codelity.inventory.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.common.EventStream;
import uk.co.codelity.event.sourcing.common.Metadata;
import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateLoadException;
import uk.co.codelity.event.sourcing.core.service.AggregateService;
import uk.co.codelity.inventory.aggregate.ProductStock;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;

import java.util.UUID;
import java.util.stream.Stream;

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

    @Mock
    private EventStream eventStream;

    @InjectMocks
    private StockService stockService;

    @Test
    void shouldAppendStockIncreasedEvent() throws AggregateLoadException, EventPersistenceException, EventLoadException {
        UUID productId = UUID.randomUUID();
        Metadata metadata = new Metadata(UUID.randomUUID(), "UserId");
        Stream<Envelope<?>> events = Stream.of(new Envelope<>(metadata, new StockIncreased(productId, 1)));
        when(aggregateService.load(eventStream, ProductStock.class)).thenReturn(productStock);
        when(productStock.supply(productId, 1, metadata)).thenReturn(events);
        when(eventStore.getStreamById(productId.toString())).thenReturn(eventStream);

        stockService.supply(productId, 1, metadata);

        verify(eventStream, times(1)).append(events);
    }

    @Test
    void shouldAppendStockDecreasedEvent() throws AggregateLoadException, EventPersistenceException, EventLoadException {
        UUID productId = UUID.randomUUID();
        Metadata metadata = new Metadata(UUID.randomUUID(), "UserId");
        Stream<Envelope<?>> events = Stream.of(new Envelope<>(metadata, new StockDecreased(productId, 1)));
        when(aggregateService.load(eventStream, ProductStock.class)).thenReturn(productStock);
        when(productStock.dispatch(productId, 1, metadata)).thenReturn(events);
        when(eventStore.getStreamById(productId.toString())).thenReturn(eventStream);

        stockService.dispatch(productId, 1, metadata);

        verify(eventStream, times(1)).append(events);
    }
}