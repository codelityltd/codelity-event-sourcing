package uk.co.codelity.event.sourcing.core.context;

import java.util.function.BiConsumer;

public class EventSubscription {
    public final String handlerCode;
    public final Class<?> handlerClass;
    public final BiConsumer handlerProxy;

    public EventSubscription(String handlerCode, Class<?> handlerClass, BiConsumer handlerProxy) {
        this.handlerCode = handlerCode;
        this.handlerClass = handlerClass;
        this.handlerProxy = handlerProxy;
    }
}
