package uk.co.codelity.jdbc.eventstore.mappers;

import uk.co.codelity.jdbc.eventstore.entity.Event;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.exception.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import static uk.co.codelity.jdbc.eventstore.repository.utils.JdbcUtils.toLocalDateTime;

public class EventDeliveryMapper {
    private EventDeliveryMapper() {
    }

    public static EventDelivery map(ResultSet resultSet) {
        return map(resultSet, false);
    }

    public static EventDelivery mapWithEvent(ResultSet resultSet) {
        return map(resultSet, true);
    }

    private static EventDelivery map(ResultSet resultSet, boolean mapEvent) {
        try {
            Long id = resultSet.getLong("delivery_id");
            String streamId = resultSet.getString("stream_id");
            Integer deliveryOrder = resultSet.getInt("delivery_order");
            Long eventId = resultSet.getLong("event_id");
            Integer status = resultSet.getInt("status");
            Integer retryCount = resultSet.getInt("retry_count");
            UUID pickedUpBy = (UUID)resultSet.getObject("picked_up_by");
            LocalDateTime pickedUpTime = toLocalDateTime(resultSet.getTimestamp("picked_up_time"));
            String handlerCode = resultSet.getString("handler_code");


            Event event = null;

            if (mapEvent) {
                event = EventMapper.map(resultSet);
            }

            return new EventDelivery(
                    id,
                    streamId,
                    deliveryOrder,
                    eventId,
                    status,
                    retryCount,
                    pickedUpBy,
                    pickedUpTime,
                    handlerCode,
                    event
            );

        } catch (SQLException ex) {
            throw new MapperException(ex.getMessage(), ex);
        }
    }


}
