package uk.co.codelity.jdbc.eventstore.repository.helpers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;


public class DbInitializer {
    private final String url;
    private final String user;
    private final String password;

    public DbInitializer(final String url, final String user, final String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public void init() throws Exception {
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            try (final PreparedStatement statement = connection.prepareStatement(loadFile("/schema.sql"))) {
                statement.execute();
            }
        }

    }

    private String loadFile(String name) throws URISyntaxException, IOException {
        URL url = DbInitializer.class.getResource(name);
        return Files.readString(Path.of(url.toURI()));
    }
}
