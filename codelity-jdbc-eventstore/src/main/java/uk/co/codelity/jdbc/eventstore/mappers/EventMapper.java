package uk.co.codelity.jdbc.eventstore.mappers;

import uk.co.codelity.jdbc.eventstore.entity.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class EventMapper {
    public static Event map(ResultSet resultSet) {
        try {
            Long id = resultSet.getLong("event_id");
            String streamId = resultSet.getString("stream_id");
            Integer position = resultSet.getInt("position");
            String name = resultSet.getString("name");
            String metadata = resultSet.getString("metadata");
            String payload = resultSet.getString("payload");
            LocalDateTime dateCreated = resultSet.getTimestamp("date_created").toLocalDateTime();
            return new Event(id,
                    streamId,
                    position,
                    name,
                    metadata,
                    payload,
                    dateCreated);
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }
}
