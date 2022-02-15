package uk.co.codelity.inventory.service.aggregate;

import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.inventory.service.events.StockDecreased;
import uk.co.codelity.inventory.service.events.StockIncreased;
import uk.co.codelity.inventory.service.events.StockReserved;

public class InventoryItem {
    @AggregateEventHandler
    public void apply(StockIncreased stockIncreased) {

    }

    @AggregateEventHandler
    public void apply(StockDecreased stockDecreased) {

    }

    @AggregateEventHandler
    public void apply(StockReserved stockReserved) {

    }
}
