package uk.co.codelity.jdbc.eventstore.entity;

public class StreamPositionSummary {
    public final Integer maxPosition;
    public final Integer maxDeliveryOrder;
    public final Integer latestEventStatus;

    public StreamPositionSummary(Integer maxPosition, Integer maxDeliveryOrder, Integer latestEventStatus) {
        this.maxPosition = maxPosition;
        this.maxDeliveryOrder = maxDeliveryOrder;
        this.latestEventStatus = latestEventStatus;
    }

    public static StreamPositionSummary createInitialSummary() {
        return new StreamPositionSummary(0, 0, DeliveryStatus.COMPLETED);
    }

    @Override
    public String toString() {
        return "StreamPositionSummary {" +
                "maxPosition=" + maxPosition +
                ", maxDeliveryOrder=" + maxDeliveryOrder +
                ", latestEventStatus=" + latestEventStatus +
                '}';
    }
}
