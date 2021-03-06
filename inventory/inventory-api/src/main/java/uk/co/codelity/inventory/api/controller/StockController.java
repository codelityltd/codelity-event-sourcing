package uk.co.codelity.inventory.api.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import uk.co.codelity.inventory.api.contracts.DispatchRequest;
import uk.co.codelity.inventory.api.contracts.SupplyRequest;

import javax.validation.Valid;
import java.util.UUID;


@OpenAPIDefinition(info = @Info(title = "Inventory API", version = "1.0", description = "Inventory Management"))
@Tag(name = "Stock Controller")
@RequestMapping(path = "/api/v1/inventory")
public interface StockController {

    @Operation(summary = "Supply request for a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Supply request is accepted",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = SupplyRequest.class)) })
    })
    @PostMapping("/products/{productId}/supply")
    ResponseEntity<Void> supply(@PathVariable("productId") UUID productId, @Valid @RequestBody SupplyRequest request, @RequestHeader HttpHeaders httpHeaders);


    @Operation(summary = "Dispatch request for a product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "Dispatch request is accepted",
            content = { @Content(mediaType = "application/json",
                schema = @Schema(implementation = DispatchRequest.class)) })
    })
    @PostMapping("/products/{productId}/dispatch")
    ResponseEntity<Void> dispatch(@PathVariable("productId") UUID productId, @RequestBody DispatchRequest request, @RequestHeader HttpHeaders httpHeaders);
}
