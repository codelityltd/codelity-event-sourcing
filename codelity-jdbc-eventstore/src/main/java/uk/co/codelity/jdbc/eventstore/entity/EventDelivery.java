package uk.co.codelity.jdbc.eventstore.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class EventDelivery {
    public final Long id;
    public final String streamId;
    public final Integer deliveryOrder;
    public final Long eventId;
    public final Integer status;
    public final Integer retryCount;
    public final UUID pickedUpBy;
    public final LocalDateTime pickedUpTime;
    public final String handlerCode;

    public EventDelivery(Long id, String streamId, Integer deliveryOrder, Long eventId, Integer status, Integer retryCount, UUID pickedUpBy, LocalDateTime pickedUpTime, String handlerCode) {
        this.id = id;
        this.streamId = streamId;
        this.deliveryOrder = deliveryOrder;
        this.eventId = eventId;
        this.status = status;
        this.retryCount = retryCount;
        this.pickedUpBy = pickedUpBy;
        this.pickedUpTime = pickedUpTime;
        this.handlerCode = handlerCode;
    }
}
