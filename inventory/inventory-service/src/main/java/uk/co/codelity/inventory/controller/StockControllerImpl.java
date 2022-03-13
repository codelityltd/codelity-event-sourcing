package uk.co.codelity.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.event.sourcing.core.exceptions.AggregateLoadException;
import uk.co.codelity.inventory.api.contracts.DispatchRequest;
import uk.co.codelity.inventory.api.contracts.SupplyRequest;
import uk.co.codelity.inventory.api.controller.StockController;
import uk.co.codelity.inventory.service.StockService;

import java.util.UUID;

import static uk.co.codelity.inventory.utility.RequestUtils.buildMetadata;

@RestController
public class StockControllerImpl implements StockController {
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
        } catch (AggregateLoadException | EventPersistenceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }



    @Override
    public ResponseEntity<Void> dispatch(UUID productId, DispatchRequest request, HttpHeaders httpHeaders) {
        try {
            stockService.dispatch(productId, request.getQuantity(), buildMetadata(httpHeaders));
            return ResponseEntity.accepted().build();
        } catch (AggregateLoadException | EventPersistenceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
