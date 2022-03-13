package uk.co.codelity.inventory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.common.Metadata;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateLoadException;
import uk.co.codelity.event.sourcing.core.service.AggregateService;
import uk.co.codelity.inventory.aggregate.ProductStock;
import uk.co.codelity.inventory.exceptions.OutOfStockException;

import java.util.List;
import java.util.UUID;

@Service
public class StockService {
    Logger logger = LoggerFactory.getLogger(StockService.class);

    private final EventStore eventStore;
    private final AggregateService aggregateService;

    @Autowired
    public StockService(EventStore eventStore, AggregateService aggregateService) {
        this.eventStore = eventStore;
        this.aggregateService = aggregateService;
    }

    public void supply(UUID productId, Integer quantity, Metadata metadata) throws AggregateLoadException, EventPersistenceException {
        logger.info("** Supply productId: {} quantity: {}", productId, quantity);
        ProductStock productStock = aggregateService.load(productId.toString(), ProductStock.class);
        List<Envelope<?>> events = productStock.supply(productId, quantity, metadata);
        eventStore.append(productId.toString(), events);
    }

    public void dispatch(UUID productId, Integer quantity, Metadata metadata) throws AggregateLoadException, EventPersistenceException {
        logger.info("** Supply productId: {} quantity: {}", productId, quantity);
        ProductStock productStock = aggregateService.load(productId.toString(), ProductStock.class);
        List<Envelope<?>> events = productStock.dispatch(productId, quantity, metadata);
        eventStore.append(productId.toString(), events);
    }
}
