package uk.co.codelity.jdbc.eventstore.delivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepository;

import java.sql.SQLException;

import static java.util.Objects.isNull;

public class DeliveryWork implements Runnable {
    Logger logger = LoggerFactory.getLogger(DeliveryWork.class);

    private final EventDelivery eventDelivery;
    private final EventHandlerExecutorService eventHandlerExecutorService;
    private final EventDeliveryRepository eventDeliveryRepository;

    public DeliveryWork(EventDelivery eventDelivery,
                         EventHandlerExecutorService eventHandlerExecutorService,
                         EventDeliveryRepository eventDeliveryRepository) {

        this.eventDelivery = eventDelivery;
        this.eventHandlerExecutorService = eventHandlerExecutorService;
        this.eventDeliveryRepository = eventDeliveryRepository;
    }

    @Override
    public void run() {
        if (isNull(eventDelivery.event)) {
            return;
        }

        EventInfo eventInfo = convertToEventInfo(eventDelivery.event);
        try {
            eventHandlerExecutorService.execute(eventInfo, eventDelivery.handlerCode);
            deliveryCompleted(eventDelivery);
        } catch (Exception e) {
            logger.error(String.format("Event (DeliveryId: %s) could not be processed.", eventDelivery.id), e);
            deliveryFailed(eventDelivery);
        }
    }

    private void deliveryCompleted(EventDelivery eventDelivery) {
        try {
            eventDeliveryRepository.markDeliveryAsCompleted(eventDelivery);
        } catch (SQLException e) {
            logger.error(String.format("Event (DeliveryId: %s) has been processed but could not be set as completed.",
                    eventDelivery.id), e);
        }
    }

    private void deliveryFailed(EventDelivery eventDelivery) {
        try {
            eventDeliveryRepository.markDeliveryAsFailed(eventDelivery.id);
        } catch (SQLException e) {
            logger.error(String.format("Event delivery (DeliveryId: %s) failure could not be saved.", eventDelivery.id)
                    , e);
        }
    }

    private EventInfo convertToEventInfo(Event event) {
        return new EventInfo(event.streamId, event.position, event.name, event.metadata, event.payload);
    }
}
