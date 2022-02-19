package uk.co.codelity.jdbc.eventstore.repository;

import uk.co.codelity.jdbc.eventstore.entity.DeliveryStatus;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.entity.StreamPositionSummary;
import uk.co.codelity.jdbc.eventstore.exception.MapperException;
import uk.co.codelity.jdbc.eventstore.mappers.EventMapper;
import uk.co.codelity.jdbc.eventstore.mappers.StreamPositionSummaryMapper;
import uk.co.codelity.jdbc.eventstore.repository.utils.JdbcInsert;
import uk.co.codelity.jdbc.eventstore.repository.utils.JdbcQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

import static uk.co.codelity.jdbc.eventstore.repository.Sql.INSERT_EVENTLOGDELIVERY_SQL;
import static uk.co.codelity.jdbc.eventstore.repository.Sql.INSERT_EVENTLOG_SQL;
import static uk.co.codelity.jdbc.eventstore.repository.Sql.SELECT_STREAM_POSITION_SUMMARY;
import static uk.co.codelity.jdbc.eventstore.repository.utils.JdbcUtils.toTimeStamp;

public class EventRepository {

    private final String url;
    private final String user;
    private final String password;

    public EventRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public List<Event> findEventsByStreamIdOrderedByPosition(String streamId) throws SQLException, MapperException {
        try (Connection connection = connect()) {
            return JdbcQuery.query(Sql.SELECT_EVENTS_BY_STREAMID)
                    .withParams(streamId)
                    .withMapper(EventMapper::map)
                    .execute(connection);
        }
    }

    public void saveAll(String streamId, List<Event> events) throws SQLException {
        if (events.isEmpty()) {
            return;
        }

        try (Connection connection = connect()) {
            Savepoint transaction = startTransaction(connection);

            try {
                StreamPositionSummary streamPositionSummary = getStreamPositionSummary(streamId, connection);
                PositionInfo positionInfo = new PositionInfo(streamPositionSummary);

                for (Event event : events) {
                    saveEvent(event, positionInfo, connection);
                }

                connection.commit();
            } catch (SQLException | MapperException e) {
                connection.rollback(transaction);
                throw e;
            }
        }
    }

    private void saveEvent(Event event, PositionInfo positionInfo, Connection connection) throws SQLException {

        long eventId = insertEventLog(event, ++positionInfo.position, connection);

        for (String handlerCode : event.handlerCodes) {
            Integer status = DeliveryStatus.PENDING;
            if (positionInfo.previousDeliveriesAreCompleted) {
                status = DeliveryStatus.READY_TO_TRANSFER;
                positionInfo.previousDeliveriesAreCompleted = false;
            }

            EventDelivery delivery = new EventDelivery(null,
                    event.streamId,
                    ++positionInfo.deliveryOrder,
                    eventId,
                    status,
                    0,
                    null,
                    null,
                    handlerCode
            );

            insertEventDelivery(delivery, connection);
        }
    }

    private long insertEventLog(Event event, Integer position, Connection connection) throws SQLException {
        return JdbcInsert.insert(INSERT_EVENTLOG_SQL)
                .withParams(
                        event.streamId,
                        position,
                        event.name,
                        event.metadata,
                        event.payload,
                        toTimeStamp(event.dateCreated))
                .executeAndGetIdentity(connection);
    }

    private int insertEventDelivery(EventDelivery eventDelivery, Connection connection) throws SQLException {
        return JdbcInsert.insert(INSERT_EVENTLOGDELIVERY_SQL)
                .withParams(
                        eventDelivery.streamId,
                        eventDelivery.deliveryOrder,
                        eventDelivery.eventId,
                        eventDelivery.status,
                        eventDelivery.retryCount,
                        eventDelivery.handlerCode)
                .execute(connection);
    }

    private StreamPositionSummary getStreamPositionSummary(String streamId, Connection connection) throws SQLException, MapperException {
        List<StreamPositionSummary> result = JdbcQuery.query(SELECT_STREAM_POSITION_SUMMARY)
                .withParams(streamId)
                .withMapper(StreamPositionSummaryMapper::map)
                .execute(connection);

        if (result.isEmpty()) {
            return StreamPositionSummary.createInitialSummary();
        } else {
            return result.get(0);
        }
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public Savepoint startTransaction(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        return connection.setSavepoint();
    }

    static class PositionInfo {
        int position;
        int deliveryOrder;
        boolean previousDeliveriesAreCompleted;

        public PositionInfo(StreamPositionSummary streamPositionSummary) {
            this.position = streamPositionSummary.maxPosition;
            this.deliveryOrder = streamPositionSummary.maxDeliveryOrder;
            this.previousDeliveriesAreCompleted = streamPositionSummary.latestEventStatus == DeliveryStatus.COMPLETED;
        }
    }
}
