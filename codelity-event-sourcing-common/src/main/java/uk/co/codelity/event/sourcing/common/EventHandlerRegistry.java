package uk.co.codelity.event.sourcing.common;

import java.util.Collection;
/**
 *  An interface used by the EventStore to get list of event-handlers.
 */
public interface EventHandlerRegistry {
    Collection<String> getHandlersByEventName(String eventName);
}
