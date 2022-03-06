package uk.co.codelity.event.sourcing.core.context;

import java.util.function.BiConsumer;

@SuppressWarnings("java:S3740")
public class EventSubscription {
    public final String handlerCode;
    public final Class<?> handlerClass;
    public final BiConsumer handlerProxy;
    public final boolean envelopeParam;

    public EventSubscription(String handlerCode, Class<?> handlerClass, BiConsumer handlerProxy, boolean envelopeParam) {
        this.handlerCode = handlerCode;
        this.handlerClass = handlerClass;
        this.handlerProxy = handlerProxy;
        this.envelopeParam = envelopeParam;
    }
}
