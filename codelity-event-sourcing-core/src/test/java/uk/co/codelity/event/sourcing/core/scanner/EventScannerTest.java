package uk.co.codelity.event.sourcing.core.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class EventScannerTest {
    private Class<?> event1 = Event1.class;
    private Class<?> event2 = Event2.class;

    private MockedStatic<ReflectionUtility> reflectionUtilityMock;

    @BeforeEach
    void setUp() {
        reflectionUtilityMock = Mockito.mockStatic(ReflectionUtility.class);
        reflectionUtilityMock.when(() -> ReflectionUtility.getClassesWithAnnotation(any(), eq(Event.class)))
                .thenReturn(new HashSet<>(asList(event1, event2)));
    }

    @AfterEach
    void tearDown() {
        reflectionUtilityMock.close();
    }


    @Test
    void shouldReturnEventClasses() throws Exception {
        EventScanner eventScanner = new EventScanner();
        Collection<Class<?>> eventClasses = eventScanner.scanForEvents(new String[] { getClass().getPackage().getName() });
        assertThat(eventClasses, containsInAnyOrder(event1, event2));
    }

    static class Event1 { }

    static class Event2 { }

}