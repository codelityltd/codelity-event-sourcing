package uk.co.codelity.jdbc.eventstore.repository.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;

public class JdbcUtils {
    static public Integer getInteger(ResultSet rs, String strColName) throws SQLException {
        int nValue = rs.getInt(strColName);
        return rs.wasNull() ? null : nValue;
    }

    public static Timestamp toTimeStamp(LocalDateTime dateTime) {
        if (isNull(dateTime)) {
            return null;
        }
        return Timestamp.valueOf(dateTime);
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (isNull(timestamp)) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
}
