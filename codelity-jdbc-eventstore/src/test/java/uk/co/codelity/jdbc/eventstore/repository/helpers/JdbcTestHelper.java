package uk.co.codelity.jdbc.eventstore.repository.helpers;

import org.h2.tools.Server;
import uk.co.codelity.jdbc.eventstore.entity.EventDelivery;
import uk.co.codelity.jdbc.eventstore.mappers.EventDeliveryMapper;
import uk.co.codelity.jdbc.eventstore.repository.utils.JdbcQuery;
import uk.co.codelity.jdbc.eventstore.repository.utils.JdbcUpdate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class JdbcTestHelper {
    public static final String PORT = "9092";
    public static final String URL =  String.format("jdbc:h2:tcp://localhost:%s/~/test", PORT);
    public static final String USER = "sa";
    public static final String PASSWORD = "";

    public static Server startServer() throws Exception {
        Server server = Server.createTcpServer("-tcpPort", PORT, "-tcpAllowOthers", "-ifNotExists").start();
        DbInitializer dbInitializer = new DbInitializer(URL, USER, PASSWORD);
        dbInitializer.init();
        return server;
    }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void cleanUp() throws SQLException {
        try(Connection connection = connect()) {
            JdbcUpdate.update("DELETE FROM event_delivery").execute(connection);
            JdbcUpdate.update("DELETE FROM event").execute(connection);
        }
    }

    public static List<EventDelivery> getDeliveryListByStreamId (String streamId) throws SQLException {
        return JdbcQuery.query("SELECT * FROM event_delivery WHERE stream_id=? order by delivery_order")
                .withParams(streamId)
                .withMapper(EventDeliveryMapper::map)
                .execute(connect());
    }

//    public static void setAllEventsAsDelivered(String streamId) throws SQLException {
//        JdbcUpdate.update("UPDATE event_delivery SET status=? WHERE stream_id=?" )
//                .withParams(DeliveryStatus.COMPLETED, streamId)
//                .execute(connect());
//    }
}
