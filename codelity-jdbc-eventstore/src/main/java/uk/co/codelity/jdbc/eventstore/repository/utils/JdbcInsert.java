package uk.co.codelity.jdbc.eventstore.repository.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class JdbcInsert {

    private String insertStatement;
    private Object[] params;

    private JdbcInsert(String insertStatement) {
        this.insertStatement = insertStatement;
    }

    public static JdbcInsert insert(String insertStatement){
        return new JdbcInsert(insertStatement);
    }

    public JdbcInsert withParams(Object... params) {
        this.params = params;
        return this;
    }

    public int execute(Connection connection) throws SQLException {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(insertStatement);
        Objects.requireNonNull(params);

        try (final PreparedStatement statement = connection.prepareStatement(insertStatement)) {
            setStatementParams(statement);
            return statement.executeUpdate();
        }
    }


    public Long executeAndGetIdentity(Connection connection) throws SQLException {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(insertStatement);
        Objects.requireNonNull(params);

        try (final PreparedStatement statement = connection.prepareStatement(insertStatement, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParams(statement);
            int rowCount = statement.executeUpdate();
            if (rowCount == 0) {
                throw new SQLException(String.format("Insert statement has failed! SQL> %s", insertStatement));
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new SQLException(String.format("Insert statement has failed. No ID obtained. SQL> %s", insertStatement));
                }
            }
        }
    }

    private void setStatementParams(PreparedStatement statement) throws SQLException {
        for(int i = 0; i < params.length; i++){
            statement.setObject(i + 1, params[i]);
        }
    }

}
