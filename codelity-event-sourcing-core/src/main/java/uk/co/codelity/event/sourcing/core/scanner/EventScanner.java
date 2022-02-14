package uk.co.codelity.event.sourcing.core.scanner;

import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventScanner {

    public Collection<Class<?>> scanForEvents(List<String> packageNamesToBeScanned) throws Exception {
        Set<Class<?>> eventClasses = new HashSet<>();

        for (String packageName: packageNamesToBeScanned) {
            Set<Class<?>> classes = ReflectionUtility.getClassesWithAnnotation(packageName, Event.class);
            eventClasses.addAll(classes);
        }
        return Collections.unmodifiableSet(eventClasses);
    }

}
