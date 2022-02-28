package uk.co.codelity.event.sourcing.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.exceptions.EventHandlerException;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.context.EventSubscription;
import uk.co.codelity.event.sourcing.core.utils.ObjectFactory;

public class EventHandlerExecutorServiceImpl implements EventHandlerExecutorService {
    Logger logger = LoggerFactory.getLogger(EventHandlerExecutorServiceImpl.class);

    private final EventSourcingContext eventSourcingContext;
    private final ObjectFactory objectFactory;
    private final ObjectMapper objectMapper;

    public EventHandlerExecutorServiceImpl(final EventSourcingContext eventSourcingContext,
                                           final ObjectMapper objectMapper,
                                           final ObjectFactory objectFactory) {
        this.eventSourcingContext = eventSourcingContext;
        this.objectMapper = objectMapper;
        this.objectFactory = objectFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(final EventInfo eventInfo, final String handlerCode) throws EventHandlerException {
        logger.info("Executing event: {} for handler: {}", eventInfo.name, handlerCode);
        try {
            EventSubscription eventSubscription = eventSourcingContext.getEventSubscription(eventInfo.name, handlerCode);
            Object handlerObject = objectFactory.create(eventSubscription.handlerClass);
            final Class<?> eventType = eventSourcingContext.getEventType(eventInfo.name);
            final Object obj = objectMapper.readValue(eventInfo.payload, eventType);
            eventSubscription.handlerProxy.accept(handlerObject, obj);
        } catch (Exception e) {
            throw new EventHandlerException("An error occurred in EventHandler", e);
        }
    }
}
