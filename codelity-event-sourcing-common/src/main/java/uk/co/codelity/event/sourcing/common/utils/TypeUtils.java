package uk.co.codelity.event.sourcing.common.utils;

import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.exceptions.MissingEventAnnotationException;

import static java.util.Objects.requireNonNull;

public class TypeUtils {

    private TypeUtils(){
    }

    public static String eventNameOf(Object event) throws MissingEventAnnotationException {
        requireNonNull(event);

        if (!event.getClass().isAnnotationPresent(Event.class)){
            throw new MissingEventAnnotationException(event.getClass().getName());
        }

        final Event eventAnnotation = event.getClass().getAnnotation(Event.class);
        return eventAnnotation.name();
    }
}
