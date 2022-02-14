package uk.co.codelity.event.sourcing.core.scanner;

import uk.co.codelity.event.sourcing.common.annotation.EventHandler;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventHandlerScanner {

    public Collection<Method> scanForEventHandlers(List<String> packageNamesToBeScanned) throws Exception {
        Set<Method> aggregateEventHandlerMethods = new HashSet<>();
        for (String packageName: packageNamesToBeScanned) {
            Set<Method> methods = ReflectionUtility.getMethodsWithAnnotation(packageName, EventHandler.class);
            aggregateEventHandlerMethods.addAll(methods);
        }
        return Collections.unmodifiableSet(aggregateEventHandlerMethods);
    }
}
