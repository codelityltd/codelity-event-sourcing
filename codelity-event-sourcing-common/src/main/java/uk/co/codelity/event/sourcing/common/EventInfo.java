package uk.co.codelity.event.sourcing.common;

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
}