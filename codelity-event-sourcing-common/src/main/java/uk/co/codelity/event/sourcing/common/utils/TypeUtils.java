package uk.co.codelity.event.sourcing.common.utils;

import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.exceptions.MissingEventAnnotationException;

public class TypeUtils {

    private TypeUtils(){
    }

    public static String eventNameOf(Object event) throws MissingEventAnnotationException {
        if (!event.getClass().isAnnotationPresent(Event.class)){
            throw new MissingEventAnnotationException(event.getClass().getName());
        }

        final Event eventAnnotation = event.getClass().getAnnotation(Event.class);
        return eventAnnotation.name();
    }
}
