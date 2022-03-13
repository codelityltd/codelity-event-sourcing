package uk.co.codelity.inventory.events;

import uk.co.codelity.event.sourcing.common.annotation.Event;

import java.util.UUID;

@Event(name="inventory-service.stock-increased")
public class StockIncreased {
    private UUID productId;
    private int quantity;

    public StockIncreased() {
    }

    public StockIncreased(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "StockIncreased{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
