package uk.co.codelity.event.sourcing.core.scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.EventHandler;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class EventHandlerScannerTest {
    @Mock
    private Method method1;

    @Mock
    private Method method2;

    private MockedStatic<ReflectionUtility> reflectionUtilityMock;

    @BeforeEach
    void setUp() {
        reflectionUtilityMock = Mockito.mockStatic(ReflectionUtility.class);
        reflectionUtilityMock.when(() -> ReflectionUtility.getMethodsWithAnnotation(any(), eq(EventHandler.class)))
                .thenReturn(new HashSet<>(asList(method1, method2)));
    }

    @AfterEach
    void tearDown() {
        reflectionUtilityMock.close();
    }


    @Test
    void scanForEventHandlers() throws Exception {
        EventHandlerScanner eventHandlerScanner = new EventHandlerScanner();
        Collection<Method> methods = eventHandlerScanner.scanForEventHandlers(new String[] { getClass().getPackage().getName() });
        assertThat(methods, containsInAnyOrder(method1, method2));
    }
}