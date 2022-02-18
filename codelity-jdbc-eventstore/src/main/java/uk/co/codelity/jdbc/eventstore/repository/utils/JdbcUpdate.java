package uk.co.codelity.jdbc.eventstore.repository.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class JdbcUpdate {

    private String updateStatement;
    private Object[] params;

    private JdbcUpdate(String updateStatement) {
        this.updateStatement = updateStatement;
        this.params = new Object[0];
    }

    public static JdbcUpdate update(String updateStatement){
        return new JdbcUpdate(updateStatement);
    }

    public JdbcUpdate withParams(Object... params) {
        this.params = params;
        return this;
    }

    public int execute(Connection connection) throws SQLException {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(updateStatement);

        try (final PreparedStatement statement = connection.prepareStatement(updateStatement)) {
            setStatementParams(statement);
            return statement.executeUpdate();
        }
    }

    private void setStatementParams(PreparedStatement statement) throws SQLException {
        for(int i = 0; i < params.length; i++){
            statement.setObject(i + 1, params[i]);
        }
    }

}
