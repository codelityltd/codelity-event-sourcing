package uk.co.codelity.inventory.aggregate;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.Metadata;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.inventory.events.StockDecreased;
import uk.co.codelity.inventory.events.StockIncreased;
import uk.co.codelity.inventory.exceptions.OutOfStockException;

import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductStock {
    private UUID productId;
    private int stock;

    public ProductStock() {
        this.stock = 0;
    }

    @AggregateEventHandler
    public void apply(StockIncreased stockIncreased) {
        if (isNull(productId)) {
            productId = stockIncreased.getProductId();
        }

        stock += stockIncreased.getQuantity();
    }

    @AggregateEventHandler
    public void apply(StockDecreased stockDecreased) {
        if (isNull(productId)) {
            productId = stockDecreased.getProductId();
        }

        stock -= stockDecreased.getQuantity();
    }

    public Stream<Envelope<Object>> supply(UUID productId, int quantity, Metadata metadata) {
        return Stream.of(new Envelope<>(metadata, new StockIncreased(productId, quantity)));
    }

    public Stream<Envelope<Object>> dispatch(UUID productId, Integer quantity, Metadata metadata) {
        if (stock < quantity) {
            throw new OutOfStockException("Product is out of stock. ProductId: " + productId);
        }

        return Stream.of(new Envelope<>(metadata, new StockDecreased(productId, quantity)));
    }
}
