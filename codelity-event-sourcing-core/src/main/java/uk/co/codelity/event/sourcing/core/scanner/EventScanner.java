package uk.co.codelity.event.sourcing.core.scanner;

import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class EventScanner {

    public Collection<Class<?>> scanForEvents(String[] packageNamesToBeScanned)
            throws IOException, URISyntaxException, ClassNotFoundException {
        requireNonNull(packageNamesToBeScanned);

        Set<Class<?>> eventClasses = new HashSet<>();

        for (String packageName: packageNamesToBeScanned) {
            Set<Class<?>> classes = ReflectionUtility.getClassesWithAnnotation(packageName, Event.class);
            eventClasses.addAll(classes);
        }
        return Collections.unmodifiableSet(eventClasses);
    }

}
