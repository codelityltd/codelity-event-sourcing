package uk.co.codelity.jdbc.eventstore.entity;

public class DeliveryStatus {
    public static final int PENDING = 0;
    public static final int READY_TO_TRANSFER = 1;
    public static final int PROCESSING = 2;
    public static final int COMPLETED = 3;
    public static final int FAILED = 5;

    private DeliveryStatus() {
    }
}
