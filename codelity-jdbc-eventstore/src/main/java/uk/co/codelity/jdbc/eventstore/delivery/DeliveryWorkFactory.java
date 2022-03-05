package uk.co.codelity.jdbc.eventstore.delivery;

import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;

public class DeliveryWorkFactory {
    public DeliveryWork create(EventDelivery eventDelivery,
                                      EventHandlerExecutorService eventHandlerExecutorService,
                                      EventDeliveryRepository eventDeliveryRepository) {

        return new DeliveryWork(eventDelivery, eventHandlerExecutorService, eventDeliveryRepository);
    }
}
