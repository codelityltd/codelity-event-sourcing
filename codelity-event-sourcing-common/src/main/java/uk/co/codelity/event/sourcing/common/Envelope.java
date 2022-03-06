package uk.co.codelity.event.sourcing.common;

public class Envelope<T>{
    public final Metadata metadata;
    public final T payload;

    public Envelope(Metadata metadata, T payload) {
        this.metadata = metadata;
        this.payload = payload;
    }
}
