package uk.co.codelity.event.sourcing.core.bootstrap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.AggregateEventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.annotation.EventHandlerScan;
import uk.co.codelity.event.sourcing.common.annotation.EventScan;
import uk.co.codelity.event.sourcing.common.annotation.EventSourcingEnabled;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.scanner.AggregateEventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventHandlerScanner;
import uk.co.codelity.event.sourcing.core.scanner.EventScanner;

import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BootstrapperTest {
    public static final String CUSTOM_EVENTS_PKG = "com.events";
    public static final String CUSTOM_EVENT_HANDLER_PKG = "com.event.handlers";
    public static final String CUSTOM_AGGREGATE_EVENT_HANDLER_PKG = "com.aggregate.event.handlers";

    Class<?> event1 = Event1.class;
    Class<?> event2 = Event2.class;

    @Mock
    Method eventHandler1;

    @Mock
    Method eventHandler2;

    @Mock
    Method aggregateEventHandler1;

    @Mock
    Method aggregateEventHandler2;


    @Mock
    EventScanner eventScanner;

    @Mock
    EventHandlerScanner eventHandlerScanner;

    @Mock
    AggregateEventHandlerScanner aggregateEventHandlerScanner;

    @Captor
    ArgumentCaptor<String[]> packagesCaptor;

    @InjectMocks
    Bootstrapper bootstrapper;

    @Test
    void shouldNotScanWhenEventSourcingNotEnabled() throws Exception {
        EventSourcingContext eventSourcingContext = bootstrapper.initContext(App.class);

        assertThat(eventSourcingContext.getEventClasses(), is(notNullValue()));
        assertThat(eventSourcingContext.getEventHandlerMethods(), is(notNullValue()));
        assertThat(eventSourcingContext.getAggregateEventHandlerMethods(), is(notNullValue()));

        verify(eventScanner, never()).scanForEvents(any());
        verify(eventHandlerScanner, never()).scanForEventHandlers(any());
        verify(aggregateEventHandlerScanner, never()).scanForAggregateEventHandlers(any());
    }

    @Test
    void shouldScanAppPackageWhenEventSourcingEnabled() throws Exception {
        String appPackage = getClass().getPackageName();

        when(eventScanner.scanForEvents(any()))
                .thenReturn(List.of(event1, event2));

        when(eventHandlerScanner.scanForEventHandlers(any()))
                .thenReturn(List.of(eventHandler1, eventHandler2));

        when(aggregateEventHandlerScanner.scanForAggregateEventHandlers(any()))
                .thenReturn(List.of(aggregateEventHandler1, aggregateEventHandler2));

        EventSourcingContext eventSourcingContext = bootstrapper.initContext(AppEventSourcingEnabled.class);

        assertThat(eventSourcingContext.getEventClasses(), is(List.of(event1, event2)));
        assertThat(eventSourcingContext.getEventHandlerMethods(), is(List.of(eventHandler1, eventHandler2)));
        assertThat(eventSourcingContext.getAggregateEventHandlerMethods(), is(List.of(aggregateEventHandler1, aggregateEventHandler2)));

        verify(eventScanner, times(1)).scanForEvents(packagesCaptor.capture());
        assertThat(packagesCaptor.getValue(), is(new String[]{ appPackage }));

        verify(eventHandlerScanner, times(1)).scanForEventHandlers(packagesCaptor.capture());
        assertThat(packagesCaptor.getValue(), is(new String[]{ appPackage }));

        verify(aggregateEventHandlerScanner, times(1)).scanForAggregateEventHandlers(packagesCaptor.capture());
        assertThat(packagesCaptor.getValue(), is(new String[]{ appPackage }));
    }


    @Test
    void shouldScanCustomPackagesWhenEventSourcingEnabled() throws Exception {
        bootstrapper.initContext(AppEventSourcingEnabledWithCustomPackages.class);

        verify(eventScanner, times(1)).scanForEvents(packagesCaptor.capture());
        assertThat(packagesCaptor.getValue(), is(new String[]{ CUSTOM_EVENTS_PKG }));

        verify(eventHandlerScanner, times(1)).scanForEventHandlers(packagesCaptor.capture());
        assertThat(packagesCaptor.getValue(), is(new String[]{ CUSTOM_EVENT_HANDLER_PKG }));

        verify(aggregateEventHandlerScanner, times(1)).scanForAggregateEventHandlers(packagesCaptor.capture());
        assertThat(packagesCaptor.getValue(), is(new String[]{ CUSTOM_AGGREGATE_EVENT_HANDLER_PKG }));
    }

    static class App {

    }

    @EventSourcingEnabled
    static class AppEventSourcingEnabled {

    }

    @EventSourcingEnabled
    @EventScan(basePackages = CUSTOM_EVENTS_PKG)
    @EventHandlerScan(basePackages = CUSTOM_EVENT_HANDLER_PKG)
    @AggregateEventHandlerScan(basePackages = CUSTOM_AGGREGATE_EVENT_HANDLER_PKG)
    static class AppEventSourcingEnabledWithCustomPackages {

    }


    @Event(name="Event-1")
    static class Event1 {

    }

    @Event(name="Event-2")
    static class Event2 {

    }
}