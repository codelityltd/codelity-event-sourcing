package uk.co.codelity.jdbc.eventstore.mappers;

import uk.co.codelity.jdbc.eventstore.entity.StreamPositionSummary;
import uk.co.codelity.jdbc.eventstore.exception.MapperException;

import java.sql.ResultSet;
import java.sql.SQLException;

import static uk.co.codelity.jdbc.eventstore.repository.utils.JdbcUtils.getInteger;

public class StreamPositionSummaryMapper {
    private StreamPositionSummaryMapper() {
    }

    public static StreamPositionSummary map(ResultSet resultSet) throws MapperException {
        try {
            Integer maxPosition = getInteger(resultSet, "position");
            Integer maxDeliveryOrder = getInteger(resultSet, "delivery_order");
            Integer latestStatus = getInteger(resultSet, "status");
            return new StreamPositionSummary(maxPosition, maxDeliveryOrder, latestStatus);
        } catch (SQLException ex) {
            throw new MapperException(ex.getMessage(), ex);
        }
    }

}
