package uk.co.codelity.inventory.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.codelity.inventory.api.contracts.DispatchRequest;
import uk.co.codelity.inventory.api.contracts.ReservationRequest;
import uk.co.codelity.inventory.api.contracts.SupplyRequest;

@RestController
@RequestMapping(path = "/inventory")
public interface InventoryController {
    @PostMapping("/supply")
    ResponseEntity<Void> supply(SupplyRequest request);

    @PostMapping("/dispatch")
    ResponseEntity<Void> dispatch(DispatchRequest request);

    @PostMapping("/reserve")
    ResponseEntity<Void> reserve(ReservationRequest request);
}
