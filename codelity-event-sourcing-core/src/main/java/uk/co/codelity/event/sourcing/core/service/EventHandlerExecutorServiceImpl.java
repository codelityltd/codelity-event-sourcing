package uk.co.codelity.event.sourcing.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.EventHandlerExecutorService;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.exceptions.EventHandlerException;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
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

    @Override
    public void execute(final EventInfo eventInfo, final String handlerCode) throws EventHandlerException {
        logger.info("Executing event:" + eventInfo.name + " for handler:" + handlerCode);
    }
}
