package uk.co.codelity.jdbc.eventstore.repository.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class JdbcQuery <T>{

    private String query;
    private Object[] params;
    private Function<ResultSet, T> mapper;

    private JdbcQuery(String query) {
        this.query = query;
        this.params = new Object[0];
    }

    public static <T> JdbcQuery<T> query(String query){
        return new JdbcQuery<>(query);
    }

    public JdbcQuery<T> withParams(Object... params) {
        this.params = params;
        return this;
    }

    public JdbcQuery<T> withMapper(Function<ResultSet, T> mapper) {
        this.mapper = mapper;
        return this;
    }

    public List<T> execute(Connection connection) throws SQLException {
        Objects.requireNonNull(connection);
        Objects.requireNonNull(query);
        Objects.requireNonNull(mapper);

        try(final PreparedStatement statement = connection.prepareStatement(this.query)) {
            setStatementParams(statement);
            final ResultSet resultSet = statement.executeQuery();

            List<T> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(mapper.apply(resultSet));
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
