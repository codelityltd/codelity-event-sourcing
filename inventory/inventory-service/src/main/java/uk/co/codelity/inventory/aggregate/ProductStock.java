package uk.co.codelity.inventory.aggregate;

import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;
import uk.co.codelity.inventory.events.StockReserved;

import java.util.List;

public class ProductStock {
    @AggregateEventHandler
    public void apply(StockIncreased stockIncreased) {

    }

    @AggregateEventHandler
    public void apply(StockDecreased stockDecreased) {

    }

    @AggregateEventHandler
    public void apply(StockReserved stockReserved) {

    }

    public List<Object> supply(int quantity) {
        return null;
    }
}
