package uk.co.codelity.inventory.service.events;

import uk.co.codelity.event.sourcing.common.annotation.Event;

@Event(name="inventory-service.stock-increased")
public class StockIncreased {
}
