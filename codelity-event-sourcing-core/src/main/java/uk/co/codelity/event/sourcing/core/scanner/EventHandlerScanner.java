package uk.co.codelity.event.sourcing.core.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.annotation.EventHandler;
import uk.co.codelity.event.sourcing.common.annotation.EventHandlerScan;
import uk.co.codelity.event.sourcing.core.bootstrap.Bootstrapper;
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

public class EventHandlerScanner {

    Logger logger = LoggerFactory.getLogger(EventHandlerScanner.class);

    public Collection<Method> scanForEventHandlers(String applicationPackageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        requireNonNull(applicationPackageName);

        String[] packageNamesToBeScanned = eventHandlerPackages(applicationPackageName);

        String packagesAsStr = String.join(",", packageNamesToBeScanned);
        logger.info("ApplicationEventListener is scanning for event handler methods. Packages are being scanned {}", packagesAsStr);

        Set<Method> aggregateEventHandlerMethods = new HashSet<>();
        for (String packageName: packageNamesToBeScanned) {
            Set<Method> methods = ReflectionUtility.getMethodsWithAnnotation(packageName, EventHandler.class);
            aggregateEventHandlerMethods.addAll(methods);
        }
        return Collections.unmodifiableSet(aggregateEventHandlerMethods);
    }

    private String[] eventHandlerPackages(String applicationPackageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, EventHandlerScan.class);
        if (clazz.isPresent()) {
            EventHandlerScan eventScan = clazz.get().getAnnotation(EventHandlerScan.class);
            if (nonNull(eventScan)) {
                return eventScan.basePackages();
            }
        }

        return new String[] { applicationPackageName };
    }
}
