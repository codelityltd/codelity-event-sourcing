package uk.co.codelity.inventory.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import uk.co.codelity.inventory.api.contracts.SupplyRequest;
import uk.co.codelity.inventory.service.StockService;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StockControllerImplTest {
    @Mock
    private StockService stockService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSupplyResponseAccepted() throws Exception {
        mvc.perform(post("/api/v1/inventory/products/{productId}/supply", UUID.randomUUID())
                        .content(aValidSupplyPayload())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldSupplyResponseBadRequestForInvalidInput() throws Exception {
        mvc.perform(post("/api/v1/inventory/products/{productId}/supply", UUID.randomUUID())
                        .content(anInvalidSupplyPayload())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private String aValidSupplyPayload() throws JsonProcessingException {
        SupplyRequest supplyRequest = new SupplyRequest();
        supplyRequest.setQuantity(2);
        return objectMapper.writeValueAsString(supplyRequest);
    }

    private String anInvalidSupplyPayload() throws JsonProcessingException {
        SupplyRequest supplyRequest = new SupplyRequest();
        return objectMapper.writeValueAsString(supplyRequest);
    }
}