package uk.co.codelity.jdbc.eventstore.mappers;

import uk.co.codelity.jdbc.eventstore.entity.StreamPositionSummary;

import java.sql.ResultSet;
import java.sql.SQLException;

import static uk.co.codelity.jdbc.eventstore.repository.utils.JdbcUtils.getInteger;

public class StreamPositionSummaryMapper {

    public static StreamPositionSummary map(ResultSet resultSet) {
        try {
            Integer maxPosition = getInteger(resultSet, "position");
            Integer maxDeliveryOrder = getInteger(resultSet, "delivery_order");
            Integer latestStatus = getInteger(resultSet, "status");
            return new StreamPositionSummary(maxPosition, maxDeliveryOrder, latestStatus);
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

}
