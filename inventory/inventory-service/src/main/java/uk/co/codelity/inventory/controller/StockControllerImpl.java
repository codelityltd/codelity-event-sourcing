package uk.co.codelity.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.co.codelity.inventory.api.contracts.DispatchRequest;
import uk.co.codelity.inventory.api.contracts.ReservationRequest;
import uk.co.codelity.inventory.api.contracts.SupplyRequest;
import uk.co.codelity.inventory.api.controller.StockController;
import uk.co.codelity.inventory.service.StockService;

import java.util.UUID;

@RestController
public class StockControllerImpl implements StockController {
    private final StockService stockService;

    @Autowired
    public StockControllerImpl(StockService stockService) {
        this.stockService = stockService;
    }


    @Override
    public ResponseEntity<Void> supply(UUID productId, SupplyRequest request) {
        stockService.supply(productId, request.getQuantity());
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> dispatch(UUID productId, DispatchRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Void> reserve(UUID productId, ReservationRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<Void> cancelReservation(UUID productId, Integer reservationId) {
        return null;
    }

    @Override
    public ResponseEntity<Void> completeReservation(UUID productId, Integer reservationId) {
        return null;
    }
}
