package uk.co.codelity.inventory.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StockService {
    Logger logger = LoggerFactory.getLogger(StockService.class);

    public void supply(UUID productId, Integer quantity) {
        logger.info("** Supply productId: {} quantity: {}", productId, quantity);

    }
}
