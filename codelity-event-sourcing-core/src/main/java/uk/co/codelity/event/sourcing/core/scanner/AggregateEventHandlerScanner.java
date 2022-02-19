package uk.co.codelity.event.sourcing.core.scanner;

import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class AggregateEventHandlerScanner {

    public Collection<Method> scanForAggregateEventHandlers(String[] packageNamesToBeScanned) throws Exception {
        requireNonNull(packageNamesToBeScanned);

        Set<Method> aggregateEventHandlerMethods = new HashSet<>();
        for (String packageName: packageNamesToBeScanned) {
            Set<Method> methods = ReflectionUtility.getMethodsWithAnnotation(packageName, AggregateEventHandler.class);
            aggregateEventHandlerMethods.addAll(methods);
        }

        return Collections.unmodifiableSet(aggregateEventHandlerMethods);
    }
}
