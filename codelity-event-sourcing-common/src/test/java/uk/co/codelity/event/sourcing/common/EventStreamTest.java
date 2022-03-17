package uk.co.codelity.event.sourcing.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.codelity.event.sourcing.common.annotation.Event;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventStreamTest {

    public static final String EVENT_1 = "Event1";
    public static final String EVENT_2 = "Event2";

    @Mock
    EventStore eventStore;

    @Spy
    ObjectMapper objectMapper = new ObjectMapper();

    @Captor
    ArgumentCaptor<Stream<EventInfo>> eventsArgumentCaptor;

    @Test
    void shouldAppendToStream() throws EventPersistenceException {
        String streamId = UUID.randomUUID().toString();
        Integer position = 2;

        List<EventInfo> eventInfoList = List.of(
                eventInfo(streamId, 1),
                eventInfo(streamId, 2));

        List<Envelope<?>> envelopes = List.of(
                envelope(new Event1()),
                envelope(new Event2())
        );

        EventStream eventStream = new EventStream(eventStore, objectMapper, streamId, position, eventInfoList);
        eventStream.append(envelopes.stream());

        verify(eventStore, times(1)).append(eq(streamId), eventsArgumentCaptor.capture());
        List<EventInfo> actualEvents = eventsArgumentCaptor.getValue().collect(Collectors.toUnmodifiableList());
        assertThat(actualEvents.size(), is(2));
        assertThat(actualEvents.get(0).position, is(3));
        assertThat(actualEvents.get(0).name, is(EVENT_1));
        assertThat(actualEvents.get(1).position, is(4));
        assertThat(actualEvents.get(1).name, is(EVENT_2));

    }

    private EventInfo eventInfo(String streamId, Integer position) {
        return new EventInfo(streamId, position, "", "", "");
    }

    private Envelope<?> envelope(Object event) {
        return new Envelope<>(new Metadata(UUID.randomUUID(), "UserId"), event);
    }

    @Event(name= EVENT_1)
    static class Event1 {}

    @Event(name= EVENT_2)
    static class Event2 {}
}