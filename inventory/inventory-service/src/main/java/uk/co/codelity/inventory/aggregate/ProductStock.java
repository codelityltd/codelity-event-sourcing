package uk.co.codelity.inventory.aggregate;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;
import uk.co.codelity.inventory.events.StockReserved;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductStock {
    private int stock;

    public ProductStock() {
        this.stock = 0;
    }

    @AggregateEventHandler
    public void apply(StockIncreased stockIncreased) {
        stock += stockIncreased.getQuantity();
    }

    @AggregateEventHandler
    public void apply(StockDecreased stockDecreased) {

    }

    @AggregateEventHandler
    public void apply(StockReserved stockReserved) {

    }

    public List<Object> supply(int quantity) {
        return List.of(new StockIncreased(quantity));
    }

}
