package uk.co.codelity.jdbc.eventstore.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.codelity.jdbc.eventstore.entity.DeliveryStatus;
import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.entity.StreamPositionSummary;
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
    Logger logger = LoggerFactory.getLogger(EventRepository.class);

    private final String url;
    private final String user;
    private final String password;

    public EventRepository(final String url, final String user, final String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public List<Event> findEventsByStreamIdOrderedByPosition(final String streamId) throws SQLException {
        try (Connection connection = connect()) {
            return JdbcQuery.query(Sql.SELECT_EVENTS_BY_STREAMID)
                    .withParams(streamId)
                    .withMapper(EventMapper::map)
                    .execute(connection);
        }
    }

    public void saveAll(final String streamId, final List<Event> events) throws SQLException {
        if (events.isEmpty()) {
            return;
        }

        try (Connection connection = connect()) {
            Savepoint transaction = startTransaction(connection);

            try {
                StreamPositionSummary streamPositionSummary = getStreamPositionSummary(streamId, connection);
                int maxPositionInStream = streamPositionSummary.maxPosition;
                int maxDeliveryOrder = streamPositionSummary.maxDeliveryOrder;
                boolean previousDeliveriesAreCompleted = streamPositionSummary.latestEventStatus == DeliveryStatus.COMPLETED;

                for (Event event : events) {
                    long eventId = insertEventLog(event, ++maxPositionInStream, connection);

                    boolean firstDelivery = true;
                    for (String handlerCode : event.handlerCodes) {
                        Integer status = DeliveryStatus.PENDING;
                        if (firstDelivery && previousDeliveriesAreCompleted) {
                            status = DeliveryStatus.READY_TO_TRANSFER;
                        }

                        EventDelivery delivery = new EventDelivery(null,
                                event.streamId,
                                ++maxDeliveryOrder,
                                eventId,
                                status,
                                0,
                                null,
                                null,
                                handlerCode
                                );

                        insertEventDelivery(delivery, connection);
                        firstDelivery = false;
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback(transaction);
                throw e;
            }
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

    private StreamPositionSummary getStreamPositionSummary(String streamId, Connection connection) throws SQLException {
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


}
