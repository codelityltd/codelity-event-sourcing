package uk.co.codelity.event.sourcing.core.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class EventScannerTest {

    private MockedStatic<ReflectionUtility> reflectionUtilityMock;

    @BeforeEach
    void setUp() {
        reflectionUtilityMock = Mockito.mockStatic(ReflectionUtility.class);
        reflectionUtilityMock.when(() -> ReflectionUtility.getClassesWithAnnotation(any(), eq(Event.class)))
                .thenReturn(new HashSet<>(asList(Event1.class, Event2.class)));
    }

    @AfterEach
    void tearDown() {
        reflectionUtilityMock.close();
    }


    @Test
    void shouldReturnEventClasses() throws Exception {
        EventScanner eventScanner = new EventScanner();
        Set<Class<?>> eventClasses = eventScanner.scanForEvents(asList(getClass().getPackage().getName()));
        assertThat(eventClasses, containsInAnyOrder(Event1.class, Event2.class));
    }

    @Event(name="event-1")
    static class Event1 {

    }

    @Event(name="event-2")
    static class Event2 {

    }
}