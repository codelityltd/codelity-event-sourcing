package uk.co.codelity.inventory.events;

import uk.co.codelity.event.sourcing.common.annotation.Event;

@Event(name="inventory-service.stock-increased")
public class StockIncreased {
    private int quantity;

    public StockIncreased() {
    }

    public StockIncreased(int quantity) {
        this.quantity = quantity;
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
                "quantity=" + quantity +
                '}';
    }
}
