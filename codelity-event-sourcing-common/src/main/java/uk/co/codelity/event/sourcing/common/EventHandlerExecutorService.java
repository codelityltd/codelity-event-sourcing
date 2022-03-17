package uk.co.codelity.event.sourcing.common;


import uk.co.codelity.event.sourcing.common.exceptions.EventHandlerException;

/**
 *  An interface used by the EventStore to execute an event-handler.
 */
public interface EventHandlerExecutorService {
    void execute(EventInfo eventInfo, String handlerCode) throws EventHandlerException;
}
