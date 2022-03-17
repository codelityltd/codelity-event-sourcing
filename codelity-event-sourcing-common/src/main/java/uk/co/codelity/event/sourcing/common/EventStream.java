package uk.co.codelity.event.sourcing.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uk.co.codelity.event.sourcing.common.exceptions.EventPersistenceException;
import uk.co.codelity.event.sourcing.common.exceptions.MissingEventAnnotationException;
import uk.co.codelity.event.sourcing.common.utils.TypeUtils;

import java.util.Iterator;
import java.util.stream.Stream;

public class EventStream {
    private final EventStore eventStore;
    private final ObjectMapper objectMapper;

    private final String streamId;
    private final Integer position;
    private final Iterable<EventInfo> events;

    public EventStream(EventStore eventStore, ObjectMapper objectMapper, String streamId, Integer position, Iterable<EventInfo> events) {
        this.eventStore = eventStore;
        this.objectMapper = objectMapper;
        this.streamId = streamId;
        this.position = position;
        this.events = events;
    }

    public String getStreamId() {
        return streamId;
    }

    public Integer getPosition() {
        return position;
    }

    public Iterable<EventInfo> getEvents() {
        return events;
    }

    public void append(Stream<Envelope<?>> envelopes) throws EventPersistenceException {
        int eventPosition = position;

        Stream.Builder<EventInfo> eventInfoStreamBuilder = Stream.builder();
        Iterator<Envelope<?>> envelopeIterator = envelopes.iterator();

        while (envelopeIterator.hasNext()) {
            Envelope<?> envelope = envelopeIterator.next();
            try {
                String eventName = TypeUtils.eventNameOf(envelope.payload);
                String payload = objectMapper.writeValueAsString(envelope.payload);
                String metadata = objectMapper.writeValueAsString(envelope.metadata);
                eventInfoStreamBuilder.add(new EventInfo(streamId, ++eventPosition, eventName, metadata, payload));
            } catch (MissingEventAnnotationException | JsonProcessingException e) {
                throw new EventPersistenceException("An error occurred while appending an event to the stream", e);
            }
        }

        eventStore.append(streamId, eventInfoStreamBuilder.build());
    }
}
