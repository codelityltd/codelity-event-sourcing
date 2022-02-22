package uk.co.codelity.event.sourcing.core.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandler;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class AggregateEventHandlerScannerTest {

    private Method method1;
    private Method method2;

    private MockedStatic<ReflectionUtility> reflectionUtilityMock;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        method1 = TestAggregate.class.getMethod("handleEvent1", TestEvent1.class);
        method2 = TestAggregate.class.getMethod("handleEvent2", TestEvent2.class);

        reflectionUtilityMock = Mockito.mockStatic(ReflectionUtility.class);
        reflectionUtilityMock.when(() -> ReflectionUtility.getMethodsWithAnnotation(any(), eq(AggregateEventHandler.class)))
                .thenReturn(new HashSet<>(asList(method1, method2)));
    }

    @AfterEach
    void tearDown() {
        reflectionUtilityMock.close();
    }

    @Test
    void shouldReturnAggregateEventHandlerMethods() throws Exception {
        AggregateEventHandlerScanner eventHandlerScanner = new AggregateEventHandlerScanner();
        Collection<Method> methods = eventHandlerScanner.scanForAggregateEventHandlers(new String[] { getClass().getPackage().getName() });
        assertThat(methods, containsInAnyOrder(method1, method2));
    }

    static class TestAggregate {
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