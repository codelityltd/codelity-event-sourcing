package uk.co.codelity.jdbc.eventstore.repository.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class JdbcQuery {

    private String query;
    private Object[] params;
    private Function<ResultSet, Object> mapper;

    private JdbcQuery(String query) {
        this.query = query;
        this.params = new Object[0];
    }

    public static JdbcQuery query(String query){
        return new JdbcQuery(query);
    }

    public JdbcQuery withParams(Object... params) {
        this.params = params;
        return this;
    }

    public JdbcQuery withMapper(Function<ResultSet, Object> mapper) {
        this.mapper = mapper;
        return this;
    }

    public <T> List<T> execute(Connection connection) throws SQLException {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(query);
        Objects.requireNonNull(mapper);

        try(final PreparedStatement statement = connection.prepareStatement(this.query)) {
            setStatementParams(statement);
            final ResultSet resultSet = statement.executeQuery();

            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add((T)mapper.apply(resultSet));
            }
            return result;
        }
    }

    private void setStatementParams(PreparedStatement statement) throws SQLException {
        for(int i = 0; i < params.length; i++){
            statement.setObject(i + 1, params[i]);
        }
    }

}
