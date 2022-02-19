package uk.co.codelity.event.sourcing.common;

import java.util.Objects;

public class EventInfo {
    public final String streamId;
    public final Integer position;
    public final String name;
    public final String metadata;
    public final String payload;

    public EventInfo(final String streamId, final Integer position, final String name, final String metadata, final String payload) {
        this.streamId = streamId;
        this.position = position;
        this.name = name;
        this.metadata = metadata;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "EventInfo {" +
                "streamId='" + streamId + '\'' +
                ", position=" + position +
                ", name='" + name + '\'' +
                ", metadata='" + metadata + '\'' +
                ", payload='" + payload + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventInfo eventInfo = (EventInfo) o;
        return streamId.equals(eventInfo.streamId) &&
                position.equals(eventInfo.position) &&
                name.equals(eventInfo.name) &&
                Objects.equals(metadata, eventInfo.metadata) &&
                payload.equals(eventInfo.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(streamId, position, name, metadata, payload);
    }
}