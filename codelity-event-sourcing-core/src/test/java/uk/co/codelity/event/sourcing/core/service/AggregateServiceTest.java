package uk.co.codelity.event.sourcing.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.EventStore;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.core.context.EventSourcingContext;
import uk.co.codelity.event.sourcing.core.utils.ObjectFactory;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregateServiceTest {
    @Mock
    private EventStore eventStore;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectFactory objectFactory;

    @Mock
    private TestAggregate aggregate;

    private EventSourcingContext eventSourcingContext;

    private AggregateService aggregateService;

    @BeforeEach
    void setUp() {
        BiConsumer<?, ?> aggregateProxy = (aggregate, event) -> ((TestAggregate)aggregate).handleEvent((TestEvent) event);
        eventSourcingContext = EventSourcingContext.builder().withEvents(Map.of("Event-1", TestEvent.class))
                .withAggregateEventHandlers(Map.of("Event-1", aggregateProxy))
                .build();

        aggregateService = new AggregateService(eventStore, eventSourcingContext, objectFactory, objectMapper);
    }

    @Test
    void shouldLoadAggregateAndRunHandlers() throws Exception {
        String streamId = randomUUID().toString();
        TestEvent event = new TestEvent();

        when(eventStore.loadEvents(streamId))
                .thenReturn(List.of(
                        new EventInfo(streamId, 1, "Event-1", "", "")));

        when(objectFactory.create(TestAggregate.class)).thenReturn(aggregate);
        when(objectMapper.readValue(any(String.class), eq(TestEvent.class))).thenReturn(event);

        TestAggregate actual = aggregateService.load(streamId, TestAggregate.class);
        assertThat(actual, is(aggregate));
        verify(aggregate, times(1)).handleEvent(eq(event));
    }

    static class TestAggregate {
        public void handleEvent(TestEvent event){}
    }

    @Event(name = "Event-1")
    static class TestEvent {}
}