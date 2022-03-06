package uk.co.codelity.inventory.handlers;

import org.springframework.stereotype.Component;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.annotation.EventHandler;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;

@Component
public class StockEventHandler {

    @EventHandler
    public void stockIncreased(Envelope<StockIncreased> stockIncreased) {
        System.out.println(stockIncreased);
    }

    @EventHandler
    public void stockDecreased(StockDecreased stockDecreased) {
        System.out.println(stockDecreased);
    }
}
