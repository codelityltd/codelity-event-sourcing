package uk.co.codelity.event.sourcing.common;

import java.util.UUID;

public class Metadata {
    private UUID correlationId;
    private String userId;

    public Metadata() {
    }

    public Metadata(UUID correlationId, String userId) {
        this.correlationId = correlationId;
        this.userId = userId;
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
