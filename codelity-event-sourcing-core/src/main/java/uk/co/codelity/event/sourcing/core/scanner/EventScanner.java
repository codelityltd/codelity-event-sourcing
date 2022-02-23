package uk.co.codelity.event.sourcing.core.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.annotation.EventScan;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class EventScanner {
    Logger logger = LoggerFactory.getLogger(EventScanner.class);

    public Collection<Class<?>> scanForEvents(String applicationPackageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        requireNonNull(applicationPackageName);

        String[] packageNamesToBeScanned = eventPackages(applicationPackageName);
        String packagesAsStr = String.join(",", packageNamesToBeScanned);
        logger.info("ApplicationEventListener is scanning for events. Packages are being scanned {}", packagesAsStr);

        Set<Class<?>> eventClasses = new HashSet<>();

        for (String packageName: packageNamesToBeScanned) {
            Set<Class<?>> classes = ReflectionUtility.getClassesWithAnnotation(packageName, Event.class);
            eventClasses.addAll(classes);
        }
        return Collections.unmodifiableSet(eventClasses);
    }

    private String[] eventPackages(String applicationPackageName)
            throws IOException, URISyntaxException, ClassNotFoundException {

        Optional<Class<?>> clazz = ReflectionUtility.getAnyClassWithAnnotation(applicationPackageName, EventScan.class);
        if (clazz.isPresent()) {
            EventScan eventScan = clazz.get().getAnnotation(EventScan.class);
            if (nonNull(eventScan)) {
                return eventScan.basePackages();
            }
        }

        return new String[]{applicationPackageName};

    }

}
