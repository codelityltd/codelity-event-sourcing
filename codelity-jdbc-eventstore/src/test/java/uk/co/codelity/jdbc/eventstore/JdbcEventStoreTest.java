package uk.co.codelity.jdbc.eventstore;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.Envelope;
import uk.co.codelity.event.sourcing.common.EventHandlerRegistry;
import uk.co.codelity.event.sourcing.common.EventInfo;
import uk.co.codelity.event.sourcing.common.Metadata;
import uk.co.codelity.event.sourcing.common.exceptions.EventLoadException;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.repository.EventRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JdbcEventStoreTest {
    public static final String EVENT_1 = "event1";
    public static final String HANDLER_1 = "HANDLER1";
    @Mock
    private EventRepository eventRepository;

    @Spy
    private ObjectMapper objectMapper;

    @Mock
    private EventHandlerRegistry eventHandlerRegistry;

    @Captor
    private ArgumentCaptor<List<Event>> eventCaptor;

    @InjectMocks
    private JdbcEventStore jdbcEventStore;

    @Test
    void shouldAppendEvents() throws EventPersistenceException, SQLException {
        String streamId= UUID.randomUUID().toString();
        when(eventHandlerRegistry.getHandlersByEventName(EVENT_1)).thenReturn(List.of(HANDLER_1));
        jdbcEventStore.append(streamId, Stream.of(new EventInfo(streamId, 0, EVENT_1, "", "")));
        verify(eventRepository, times(1)).saveAll(eq(streamId), eventCaptor.capture());
        assertThat(eventCaptor.getValue().size(), is(1));
        assertThat(eventCaptor.getValue().get(0).name, is(EVENT_1));
        assertThat(eventCaptor.getValue().get(0).handlerCodes, is(List.of(HANDLER_1)));
    }

    @Test
    void appendEventsShouldThrowEventPeristenceException() throws SQLException {
        String streamId= UUID.randomUUID().toString();
        when(eventHandlerRegistry.getHandlersByEventName(EVENT_1)).thenReturn(List.of(HANDLER_1));
        doThrow(new SQLException()).when(eventRepository).saveAll(any(), any());
        assertThrows(EventPersistenceException.class, () -> jdbcEventStore.append(streamId,
                Stream.of(new EventInfo(streamId, 0, EVENT_1, "", ""))));
    }

    @Test
    void shouldLoadEvents() throws SQLException, EventLoadException {
        String streamId = UUID.randomUUID().toString();

        when(eventRepository.findEventsByStreamIdOrderedByPosition(any()))
                .thenReturn(asList(createEventLog(streamId, 1), createEventLog(streamId, 2)));

        Iterable<EventInfo> actual = jdbcEventStore.loadEvents("AnyStreamId");

        assertThat(actual, is(List.of(createEventInfo(streamId, 1), createEventInfo(streamId, 2))));
    }

    @Test
    void loadEventsShouldThrowEventLoadException() throws SQLException {
        String streamId= UUID.randomUUID().toString();
        doThrow(new SQLException()).when(eventRepository).findEventsByStreamIdOrderedByPosition(streamId);
        assertThrows(EventLoadException.class, () -> jdbcEventStore.loadEvents(streamId));
    }

    private Event createEventLog(final String streamId, final int position) {
        return new Event(
                null,
                streamId,
                position,
                "Event-" + position,
                "",
                "<payload>",
                LocalDateTime.now());
    }

    private EventInfo createEventInfo(final String streamId, final int position) {
        return new EventInfo(
                streamId,
                position,
                "Event-" + position,
                "",
                "<payload>");
    }

    @uk.co.codelity.event.sourcing.common.annotation
            .Event(name = "event1")
    static class Event1 {
        String name;
    }
}