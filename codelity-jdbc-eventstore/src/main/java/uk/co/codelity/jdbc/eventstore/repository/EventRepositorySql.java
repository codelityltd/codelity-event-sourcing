package uk.co.codelity.jdbc.eventstore.repository;

public class EventRepositorySql {
    static final String SELECT_STREAM_POSITION_SUMMARY = "select position, delivery_order, status from event_delivery eld  " +
            "right join event el on el.event_id =eld.event_id  " +
            "where el.stream_id =? order by delivery_order desc limit 1;";

    static final String INSERT_EVENTLOG_SQL = "INSERT INTO event " +
            "(stream_id, position, name, metadata, payload, date_created) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    static final String INSERT_EVENTLOGDELIVERY_SQL = "INSERT INTO event_delivery " +
            "(stream_id, delivery_order, event_id, status, retry_count,  handler_code) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    static final String SELECT_EVENTS_BY_STREAMID = "select * from event where stream_id=? order by position asc";

    private EventRepositorySql() {
    }
}
