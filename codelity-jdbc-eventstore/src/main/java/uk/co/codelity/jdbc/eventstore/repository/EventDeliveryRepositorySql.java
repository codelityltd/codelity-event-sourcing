package uk.co.codelity.jdbc.eventstore.repository;

public class EventDeliveryRepositorySql {
    private static final String PICK_ITEMS = "SELECT delivery_id FROM event_delivery WHERE " +
            "status = 1 " /* READY_TO_PICK_UP */+
            "OR (" +
            "status = 4 " /* FAILED */+
            "AND retry_count < ? " /* LESS THEN MAX_RETRY_COUNT */ +
            "AND extract(epoch from (now() - picked_up_time)) > ?) " /* RETRY_INTERVAL REACHED */+
            "limit ?";

    static final String PICK_AND_MARK_LOGITEMS_TO_BE_DELIVERED =
            String.format("UPDATE event_delivery SET " +
                    "status=2, " +
                    "picked_up_by=?, " +
                    "picked_up_time=now(), " +
                    "retry_count=retry_count + 1 " +
                    "WHERE delivery_id IN (%s); ", PICK_ITEMS);

    static final String SELECT_LOGITEMS_PREVIOUSLY_MARKED = "SELECT * FROM event_delivery d " +
            "INNER JOIN event el ON el.event_id=d.event_id " +
            "WHERE d.status=2 AND d.picked_up_by=?;";


    static final String UPDATE_DELIVERY_STATUS = "update event_delivery set status=? where delivery_id=?;";

    static final String MARK_NEXT_DELIVERY_AS_READY = "UPDATE event_delivery SET status=1 WHERE stream_id=? AND delivery_order=?";

    private EventDeliveryRepositorySql() {
    }
}
