package uk.co.codelity.inventory.events;

import uk.co.codelity.event.sourcing.common.annotation.Event;

@Event(name="inventory-service.stock-decreased")
public class StockDecreased {
    private int quantity;

    public StockDecreased() {
    }

    public StockDecreased(int quantity) {
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
        return "StockDecreased{" +
                "quantity=" + quantity +
                '}';
    }
}
