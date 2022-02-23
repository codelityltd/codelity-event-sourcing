package uk.co.codelity.event.sourcing.core.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.annotation.EventHandler;
import uk.co.codelity.event.sourcing.common.annotation.EventHandlerScan;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.lang.reflect.Method;
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
class EventHandlerScannerTest {
    public static final String CUSTOM_PACKAGE_NAME = "custom.package.name";
    public static final String CUSTOM_EVENT_HANDLER_PKG = "com.event.handler.packages";

    private Method method1;
    private Method method2;

    private MockedStatic<ReflectionUtility> reflectionUtilityMock;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        method1 = TestEventListener.class.getMethod("handleEvent1", TestEvent1.class);
        method2 = TestEventListener.class.getMethod("handleEvent2", TestEvent2.class);

        reflectionUtilityMock = Mockito.mockStatic(ReflectionUtility.class);
        reflectionUtilityMock.when(() -> ReflectionUtility.getMethodsWithAnnotation(any(), eq(EventHandler.class)))
                .thenReturn(new HashSet<>(asList(method1, method2)));

        reflectionUtilityMock.when(() -> ReflectionUtility.getAnyClassWithAnnotation(eq(CUSTOM_PACKAGE_NAME), eq(EventHandlerScan.class)))
                .thenReturn(Optional.of(CustomPkgTestApp.class));
    }

    @AfterEach
    void tearDown() {
        reflectionUtilityMock.close();
    }


    @Test
    void shouldReturnEventHandlers() throws Exception {
        EventHandlerScanner eventHandlerScanner = new EventHandlerScanner();
        Collection<Method> methods = eventHandlerScanner.scanForEventHandlers(getClass().getPackage().getName());
        assertThat(methods, containsInAnyOrder(method1, method2));
        reflectionUtilityMock.verify(() -> ReflectionUtility.getMethodsWithAnnotation(eq(getClass().getPackage().getName()), eq(EventHandler.class)), times(1));
    }

    @Test
    void shouldReturnEventHandlersInCustomPackage() throws Exception {
        EventHandlerScanner eventHandlerScanner = new EventHandlerScanner();
        Collection<Method> methods = eventHandlerScanner.scanForEventHandlers(CUSTOM_PACKAGE_NAME);
        assertThat(methods, containsInAnyOrder(method1, method2));
        reflectionUtilityMock.verify(() -> ReflectionUtility.getMethodsWithAnnotation(eq(CUSTOM_EVENT_HANDLER_PKG), eq(EventHandler.class)), times(1));

    }

    @EventHandlerScan(basePackages = CUSTOM_EVENT_HANDLER_PKG)
    public class CustomPkgTestApp {}

    static class TestEventListener {
        @AggregateEventHandler
        public void handleEvent1(TestEvent1 event){}

        @AggregateEventHandler
        public void handleEvent2(TestEvent2 event){}
    }

    @Event(name="Event-1")
    static class TestEvent1 {}


    @Event(name="Event-2")
    static class TestEvent2 {}
}