package uk.co.codelity.event.sourcing.common;


import uk.co.codelity.event.sourcing.common.exceptions.EventHandlerException;

public interface EventHandlerExecutorService {
    void execute(EventInfo eventInfo, String handlerCode) throws EventHandlerException;
}
