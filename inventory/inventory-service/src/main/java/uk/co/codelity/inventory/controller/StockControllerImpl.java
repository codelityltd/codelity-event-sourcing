package uk.co.codelity.inventory.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateLoadException;
import uk.co.codelity.inventory.api.contracts.DispatchRequest;
import uk.co.codelity.inventory.api.contracts.SupplyRequest;
import uk.co.codelity.inventory.api.controller.StockController;
import uk.co.codelity.inventory.exceptions.ServiceException;
import uk.co.codelity.inventory.service.StockService;

import java.util.UUID;

import static uk.co.codelity.inventory.utility.RequestUtils.buildMetadata;

@RestController
public class StockControllerImpl implements StockController {
    Logger logger = LoggerFactory.getLogger(StockControllerImpl.class);

    private final StockService stockService;

    @Autowired
    public StockControllerImpl(StockService stockService) {
        this.stockService = stockService;
    }


    @Override
    public ResponseEntity<Void> supply(UUID productId, SupplyRequest request, HttpHeaders httpHeaders) {
        try {
            stockService.supply(productId, request.getQuantity(), buildMetadata(httpHeaders));
            return ResponseEntity.accepted().build();
        } catch (AggregateLoadException | EventLoadException | EventPersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }

    @Override
    public ResponseEntity<Void> dispatch(UUID productId, DispatchRequest request, HttpHeaders httpHeaders) {
        try {
            stockService.dispatch(productId, request.getQuantity(), buildMetadata(httpHeaders));
            return ResponseEntity.accepted().build();
        } catch (AggregateLoadException | EventLoadException | EventPersistenceException e) {
            throw new ServiceException(e.getMessage(), e);
        }
    }
}
