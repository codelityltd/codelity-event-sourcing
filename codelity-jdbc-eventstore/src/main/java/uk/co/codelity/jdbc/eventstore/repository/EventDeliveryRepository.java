package uk.co.codelity.jdbc.eventstore.repository;

import uk.co.codelity.jdbc.eventstore.entity.DeliveryStatus;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.mappers.EventDeliveryMapper;
import uk.co.codelity.jdbc.eventstore.repository.utils.JdbcQuery;
import uk.co.codelity.jdbc.eventstore.repository.utils.JdbcUpdate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;
import java.util.UUID;

import static uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepositorySql.MARK_NEXT_DELIVERY_AS_READY;
import static uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepositorySql.PICK_AND_MARK_LOGITEMS_TO_BE_DELIVERED;
import static uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepositorySql.SELECT_LOGITEMS_PREVIOUSLY_MARKED;
import static uk.co.codelity.jdbc.eventstore.repository.EventDeliveryRepositorySql.UPDATE_DELIVERY_STATUS;

public class EventDeliveryRepository {
    private final String url;
    private final String user;
    private final String password;

    public EventDeliveryRepository(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public List<EventDelivery> getEventsToBeDelivered(Integer itemCount, UUID consumerId, Integer maxRetryCount, Integer retryIntervalInSec) throws SQLException {
        try (final Connection connection = connect()) {
            JdbcUpdate.update(PICK_AND_MARK_LOGITEMS_TO_BE_DELIVERED)
                    .withParams(consumerId, maxRetryCount, retryIntervalInSec, itemCount)
                    .execute(connection);

            return JdbcQuery.<EventDelivery>query(SELECT_LOGITEMS_PREVIOUSLY_MARKED)
                    .withParams(consumerId)
                    .withMapper(EventDeliveryMapper::map)
                    .execute(connection);
        }
    }

    public void markDeliveryAsCompleted(EventDelivery delivery) throws SQLException {
        try(Connection connection = connect()) {
            Savepoint savepoint = startTransaction(connection);
            try {
                JdbcUpdate.update(UPDATE_DELIVERY_STATUS)
                        .withParams(DeliveryStatus.COMPLETED, delivery.id)
                        .execute(connection);

                int nextDeliveryOrder = delivery.deliveryOrder + 1;

                JdbcUpdate.update(MARK_NEXT_DELIVERY_AS_READY)
                        .withParams(delivery.streamId, nextDeliveryOrder)
                        .execute(connection);

                connection.commit();
            }catch (SQLException ex) {
                connection.rollback(savepoint);
                throw ex;
            }
        }
    }

    public void markDeliveryAsFailed(Long eventDeliveryId) throws SQLException {
        try(Connection connection = connect()) {
            JdbcUpdate.update(UPDATE_DELIVERY_STATUS)
                    .withParams(DeliveryStatus.FAILED, eventDeliveryId)
                    .execute(connection);
        }

    }

    private Savepoint startTransaction(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        return connection.setSavepoint();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
