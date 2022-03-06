package uk.co.codelity.event.sourcing.core.bootstrap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.annotation.EventSourcingEnabled;
import uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf.AppEventSourcingEnabled;
import uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf.Event1;
import uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf.Event2;
import uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf.InvalidEvent;
import uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf.TestAggregate;
import uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.autoconf.TestEventListener;
import uk.co.codelity.event.sourcing.core.bootstrap.testcontexts.noconfig.App;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.exceptions.BootstrapException;
import uk.co.codelity.event.sourcing.core.scanner.AggregateEventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventScanner;
import uk.co.codelity.event.sourcing.core.utils.EventHandlerCode;
import uk.co.codelity.event.sourcing.core.utils.HandlerLambdaFactory;
import uk.co.codelity.event.sourcing.core.utils.reflection.ReflectionUtility;

import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapperTest {

    Class<?> event1 = Event1.class;
    Class<?> event2 = Event2.class;
    Class<?> invalidEvent = InvalidEvent.class;

    Method eventHandler1;
    Method eventHandler2;
    Method aggregateEventHandler1;
    Method aggregateEventHandler2;
    Method multiParamsInvalidAggregateEventHandler;
    Method nonEventArgInvalidAggregateEventHandler;
    String handlerCode1;
    String handlerCode2;

    @Mock
    EventScanner eventScanner;

    @Mock
    EventHandlerScanner eventHandlerScanner;

    @Mock
    AggregateEventHandlerScanner aggregateEventHandlerScanner;

    @Captor
    ArgumentCaptor<String> appPackageCaptor;

    @InjectMocks
    Bootstrapper bootstrapper;

    @BeforeEach
    void setUp() throws NoSuchMethodException, NoSuchAlgorithmException {
        eventHandler1 = TestEventListener.class.getMethod("handleEvent1", Event1.class);
        eventHandler2 = TestEventListener.class.getMethod("handleEvent2", Envelope.class);
        aggregateEventHandler1  = TestAggregate.class.getMethod("handleEvent1", Event1.class);
        aggregateEventHandler2  = TestAggregate.class.getMethod("handleEvent2", Event2.class);
        multiParamsInvalidAggregateEventHandler  = TestAggregate.class.getMethod("invalidHandlerMultiParams", Event1.class, Integer.class);
        nonEventArgInvalidAggregateEventHandler = TestAggregate.class.getMethod("invalidHandlerNonEventArg", Integer.class);
        handlerCode1 = EventHandlerCode.generate(eventHandler1);
        handlerCode2 = EventHandlerCode.generate(eventHandler2);
    }

    @Test
    void shouldNotScanWhenEventSourcingNotEnabled() throws Exception {
        bootstrapper.initContext(App.class.getPackageName());

        verify(eventScanner, never()).scanForEvents(any());
        verify(eventHandlerScanner, never()).scanForEventHandlers(any());
        verify(aggregateEventHandlerScanner, never()).scanForAggregateEventHandlers(any());
    }

    @Test
    void shouldScanAppPackageWhenEventSourcingEnabled() throws Exception {
        String appPackage = AppEventSourcingEnabled.class.getPackageName();

        when(eventScanner.scanForEvents(any()))
                .thenReturn(List.of(event1, event2));

        when(eventHandlerScanner.scanForEventHandlers(any()))
                .thenReturn(List.of(eventHandler1, eventHandler2));

        when(aggregateEventHandlerScanner.scanForAggregateEventHandlers(any()))
                .thenReturn(List.of(aggregateEventHandler1, aggregateEventHandler2));

        EventSourcingContext eventSourcingContext = bootstrapper.initContext(appPackage);

        assertThat(eventSourcingContext.getEventType("Event-1"), is(event1));
        assertThat(eventSourcingContext.getEventType("Event-2"), is(event2));
        assertThat(eventSourcingContext.getEventSubscription("Event-1", handlerCode1), is(notNullValue()));
        assertThat(eventSourcingContext.getEventSubscription("Event-2", handlerCode2), is(notNullValue()));

        verify(eventScanner, times(1)).scanForEvents(appPackageCaptor.capture());
        assertThat(appPackageCaptor.getValue(), is(appPackage ));

        verify(eventHandlerScanner, times(1)).scanForEventHandlers(appPackageCaptor.capture());
        assertThat(appPackageCaptor.getValue(), is(appPackage));

        verify(aggregateEventHandlerScanner, times(1)).scanForAggregateEventHandlers(appPackageCaptor.capture());
        assertThat(appPackageCaptor.getValue(), is(appPackage));
    }

    @Test
    void shouldThrowBootstrapExceptionWhenInvalidAggregateEventHandlerFound() throws Exception {
        String appPackage = AppEventSourcingEnabled.class.getPackageName();

        when(eventScanner.scanForEvents(any()))
                .thenReturn(List.of(event1));

        when(aggregateEventHandlerScanner.scanForAggregateEventHandlers(any()))
                .thenReturn(List.of(multiParamsInvalidAggregateEventHandler));

        assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
    }

    @Test
    void shouldThrowBootstrapExceptionWhenNonEventArgAggregateEventHandlerFound() throws Exception {
        String appPackage = AppEventSourcingEnabled.class.getPackageName();

        when(eventScanner.scanForEvents(any()))
                .thenReturn(List.of(event1));

        when(aggregateEventHandlerScanner.scanForAggregateEventHandlers(any()))
                .thenReturn(List.of(nonEventArgInvalidAggregateEventHandler));

        assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
    }

    @Test
    void shouldThrowBootstrapExceptionWhenAggregateEventHandlerProxyThrowsError() throws Exception {
        try(MockedStatic<HandlerLambdaFactory> factoryMockedStatic = Mockito.mockStatic(HandlerLambdaFactory.class)) {
            factoryMockedStatic.when(()->HandlerLambdaFactory.createHandlerLambda(eq(TestAggregate.class), any(), any()))
                    .thenThrow(new Exception("An error occurred."));

            String appPackage = AppEventSourcingEnabled.class.getPackageName();

            when(eventScanner.scanForEvents(any()))
                    .thenReturn(List.of(event1));

            when(aggregateEventHandlerScanner.scanForAggregateEventHandlers(any()))
                    .thenReturn(List.of(aggregateEventHandler1));

            assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
        }
    }

    @Test
    void shouldThrowBootstrapExceptionWhenInvalidEventFound() throws Exception {
        String appPackage = AppEventSourcingEnabled.class.getPackageName();

        when(eventScanner.scanForEvents(any()))
                .thenReturn(List.of(invalidEvent));

        assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
    }

    @Test
    void shouldThrowBootstrapExceptionWhenEventSourcingEnabledReadError() throws Exception {
        try(MockedStatic<ReflectionUtility> factoryMockedStatic = Mockito.mockStatic(ReflectionUtility.class)) {
            factoryMockedStatic.when(()->ReflectionUtility.getClassesWithAnnotation(any(), eq(EventSourcingEnabled.class)))
                    .thenThrow(new ClassNotFoundException("An error occurred."));

            String appPackage = AppEventSourcingEnabled.class.getPackageName();
            assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
        }
    }

    @Test
    void shouldThrowBootstrapExceptionWhenEventScannerThrowsException() throws Exception {
        when(eventScanner.scanForEvents(any())).thenThrow(new ClassNotFoundException("An error occurred."));
        String appPackage = AppEventSourcingEnabled.class.getPackageName();
        assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
    }

    @Test
    void shouldThrowBootstrapExceptionWhenEventHandlerScannerThrowsException() throws Exception {
        when(eventHandlerScanner.scanForEventHandlers(any())).thenThrow(new ClassNotFoundException("An error occurred."));
        String appPackage = AppEventSourcingEnabled.class.getPackageName();
        assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
    }

    @Test
    void shouldThrowBootstrapExceptionWhenAggregateScannerThrowsException() throws Exception {
        when(aggregateEventHandlerScanner.scanForAggregateEventHandlers(any())).thenThrow(new ClassNotFoundException("An error occurred."));
        String appPackage = AppEventSourcingEnabled.class.getPackageName();
        assertThrows(BootstrapException.class, () -> bootstrapper.initContext(appPackage));
    }
}