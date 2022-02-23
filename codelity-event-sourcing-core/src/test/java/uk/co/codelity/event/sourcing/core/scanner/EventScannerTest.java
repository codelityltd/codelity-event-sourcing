package uk.co.codelity.event.sourcing.core.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.annotation.EventScan;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class EventScannerTest {
    public static final String CUSTOM_PACKAGE_NAME = "custom.package.name";
    public static final String CUSTOM_EVENT_PKG = "com.events.packages";

    private Class<?> event1 = Event1.class;
    private Class<?> event2 = Event2.class;

    private MockedStatic<ReflectionUtility> reflectionUtilityMock;

    @BeforeEach
    void setUp() {
        reflectionUtilityMock = Mockito.mockStatic(ReflectionUtility.class);
        reflectionUtilityMock.when(() -> ReflectionUtility.getClassesWithAnnotation(any(), eq(Event.class)))
                .thenReturn(new HashSet<>(asList(event1, event2)));
        reflectionUtilityMock.when(() -> ReflectionUtility.getAnyClassWithAnnotation(eq(CUSTOM_PACKAGE_NAME), eq(EventScan.class)))
                .thenReturn(Optional.of(CustomPkgTestApp.class));

    }

    @AfterEach
    void tearDown() {
        reflectionUtilityMock.close();
    }

    @Test
    void shouldReturnEventClasses() throws Exception {
        EventScanner eventScanner = new EventScanner();
        Collection<Class<?>> eventClasses = eventScanner.scanForEvents(getClass().getPackage().getName());
        assertThat(eventClasses, containsInAnyOrder(event1, event2));
        reflectionUtilityMock.verify(() -> ReflectionUtility.getClassesWithAnnotation(eq(getClass().getPackage().getName()), eq(Event.class)), times(1));
    }

    @Test
    void shouldReturnEventClassesInCustomPackage() throws Exception {
        EventScanner eventScanner = new EventScanner();
        Collection<Class<?>> eventClasses = eventScanner.scanForEvents(CUSTOM_PACKAGE_NAME);
        assertThat(eventClasses, containsInAnyOrder(event1, event2));
        reflectionUtilityMock.verify(() -> ReflectionUtility.getClassesWithAnnotation(eq(CUSTOM_EVENT_PKG), eq(Event.class)), times(1));
    }

    @EventScan(basePackages = CUSTOM_EVENT_PKG)
    public class CustomPkgTestApp {}

    static class Event1 { }

    static class Event2 { }

}