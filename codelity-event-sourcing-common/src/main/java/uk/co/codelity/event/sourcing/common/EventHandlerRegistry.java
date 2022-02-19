package uk.co.codelity.event.sourcing.common;

import java.util.Collection;

public interface EventHandlerRegistry {
    Collection<String> getHandlersByEventName(String eventName);
}
