package uk.co.codelity.inventory.service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.co.codelity.inventory.api.contracts.DispatchRequest;
import uk.co.codelity.inventory.api.contracts.ReservationRequest;
import uk.co.codelity.inventory.api.contracts.SupplyRequest;
import uk.co.codelity.inventory.api.controller.InventoryController;

@RestController
public class InventoryControllerImpl implements InventoryController {

    @Override
    public ResponseEntity<Void> supply(SupplyRequest request) {
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> dispatch(DispatchRequest request) {
        return ResponseEntity.accepted().build();
    }

    @Override
    public ResponseEntity<Void> reserve(ReservationRequest request) {
        return ResponseEntity.accepted().build();
    }
}
