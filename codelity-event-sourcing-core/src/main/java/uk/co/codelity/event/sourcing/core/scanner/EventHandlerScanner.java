package uk.co.codelity.event.sourcing.core.scanner;

import uk.co.codelity.event.sourcing.common.annotation.EventHandler;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class EventHandlerScanner {

    public Collection<Method> scanForEventHandlers(String[] packageNamesToBeScanned)
            throws IOException, URISyntaxException, ClassNotFoundException {
        requireNonNull(packageNamesToBeScanned);

        Set<Method> aggregateEventHandlerMethods = new HashSet<>();
        for (String packageName: packageNamesToBeScanned) {
            Set<Method> methods = ReflectionUtility.getMethodsWithAnnotation(packageName, EventHandler.class);
            aggregateEventHandlerMethods.addAll(methods);
        }
        return Collections.unmodifiableSet(aggregateEventHandlerMethods);
    }
}
