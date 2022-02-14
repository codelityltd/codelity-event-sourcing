package uk.co.codelity.event.sourcing.core.scanner;

import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventScanner {

    public Set<Class<?>> scanForEvents(List<String> packageNamesToBeScanned) throws Exception {
        final Set<Class<?>> eventClasses = new HashSet<>();

        for (String packageName: packageNamesToBeScanned) {
            final Set<Class<?>> classes = ReflectionUtility.getClassesWithAnnotation(packageName, Event.class);
            eventClasses.addAll(classes);
        }
        return eventClasses;
    }

}
