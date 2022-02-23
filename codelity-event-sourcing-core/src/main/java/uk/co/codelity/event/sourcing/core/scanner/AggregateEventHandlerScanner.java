package uk.co.codelity.event.sourcing.core.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandlerScan;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class AggregateEventHandlerScanner {
    Logger logger = LoggerFactory.getLogger(AggregateEventHandlerScanner.class);

    public Collection<Method> scanForAggregateEventHandlers(String applicationPackageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        requireNonNull(applicationPackageName);
        String[] packageNamesToBeScanned = aggregateEventHandlerPackages(applicationPackageName);
        String packagesAsStr = String.join(",", packageNamesToBeScanned);
        logger.info("ApplicationEventListener is scanning for aggregate event handlers. Packages are being scanned {}", packagesAsStr);

        Set<Method> aggregateEventHandlerMethods = new HashSet<>();
        for (String packageName: packageNamesToBeScanned) {
            Set<Method> methods = ReflectionUtility.getMethodsWithAnnotation(packageName, AggregateEventHandler.class);
            aggregateEventHandlerMethods.addAll(methods);
        }

        return Collections.unmodifiableSet(aggregateEventHandlerMethods);
    }


    private String[] aggregateEventHandlerPackages(String applicationPackageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, AggregateEventHandlerScan.class);
        if (clazz.isPresent()) {
            AggregateEventHandlerScan eventScan = clazz.get().getAnnotation(AggregateEventHandlerScan.class);
            if (nonNull(eventScan)) {
                return eventScan.basePackages();
            }
        }

        return new String[]{applicationPackageName};
    }
}
