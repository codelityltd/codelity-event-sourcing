package uk.co.codelity.inventory.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.annotation.EventHandler;
import uk.co.codelity.inventory.entity.Inventory;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;
import uk.co.codelity.inventory.repository.InventoryRepository;

import java.util.Optional;

@Component
public class StockEventHandler {
    Logger logger = LoggerFactory.getLogger(StockEventHandler.class);

    private final InventoryRepository inventoryRepository;

    @Autowired
    public StockEventHandler(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @EventHandler
    public void stockIncreased(Envelope<StockIncreased> envelope) {
        StockIncreased stockIncreased = envelope.payload;
        Optional<Inventory> optionalInventory = inventoryRepository.findById(stockIncreased.getProductId());
        if (optionalInventory.isPresent()){
            Inventory inventory = optionalInventory.get();
            inventory.setStock(inventory.getStock() + stockIncreased.getQuantity());
            inventoryRepository.save(inventory);
        } else {
            inventoryRepository.save(new Inventory(stockIncreased.getProductId(), stockIncreased.getQuantity()));
        }
    }

    @EventHandler
    public void stockDecreased(StockDecreased stockDecreased) {
        Optional<Inventory> optionalInventory = inventoryRepository.findById(stockDecreased.getProductId());
        if (optionalInventory.isPresent()){
            Inventory inventory = optionalInventory.get();
            boolean hasEnoughStock = inventory.getStock() >= stockDecreased.getQuantity();

            if (hasEnoughStock) {
                inventory.setStock(inventory.getStock() - stockDecreased.getQuantity());
                inventoryRepository.save(inventory);
            } else {
                logger.warn("Stock not enough. ProductId: {} Stock: {} RequestedQuantity: {}",
                        stockDecreased.getProductId(),
                        inventory.getStock(),
                        stockDecreased.getQuantity());
            }

        } else {
            logger.warn("Inventory record could not be found to decrease stock. ProductId: {}", stockDecreased.getProductId());
        }
    }
}
