package uk.co.codelity.jdbc.eventstore.entity;

import java.time.LocalDateTime;
import java.util.Collection;

@SuppressWarnings("squid:S00107")
public class Event {
    public final Long id;
    public final String streamId;
    public final Integer position;
    public final String name;
    public final String metadata;
    public final String payload;
    public final LocalDateTime dateCreated;
    public final Collection<String> handlerCodes;

    public Event(Long id, String streamId, Integer position, String name, String metadata, String payload, LocalDateTime dateCreated) {
        this.id = id;
        this.streamId = streamId;
        this.position = position;
        this.name = name;
        this.metadata = metadata;
        this.payload = payload;
        this.dateCreated = dateCreated;
        this.handlerCodes = null;
    }

    public Event(Long id, String streamId, Integer position, String name, String metadata, String payload, LocalDateTime dateCreated, Collection<String> handlerCodes) {
        this.id = id;
        this.streamId = streamId;
        this.position = position;
        this.name = name;
        this.metadata = metadata;
        this.payload = payload;
        this.dateCreated = dateCreated;
        this.handlerCodes = handlerCodes;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", streamId='" + streamId + '\'' +
                ", position=" + position +
                ", name='" + name + '\'' +
                ", metadata='" + metadata + '\'' +
                ", payload='" + payload + '\'' +
                ", dateCreated=" + dateCreated +
                ", handlerCodes=" + handlerCodes +
                '}';
    }
}
