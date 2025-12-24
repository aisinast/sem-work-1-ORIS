package ru.itis.spotty.repositories;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;
import java.util.UUID;

public class JdbcSessionRepository implements SessionRepository {
    private final String SESSIONS_TABLE_CREATE_QUERY = """
            CREATE TABLE IF NOT EXISTS sessions (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                user_id UUID NOT NULL,
                expires_at TIMESTAMP NOT NULL
            );
    """;

    private final String ADD_NEW_SESSION_QUERY = """
            INSERT INTO sessions (id, user_id, expires_at)
            VALUES (?, ?, ?);
    """;

    private final String GET_USER_ID_BY_SESSION_ID_QUERY = """
            SELECT user_id FROM sessions WHERE id = ?;
    """;

    private final String REMOVE_SESSION_QUERY = """
            UPDATE sessions
            SET expires_at = now()
            WHERE id = ?;
    """;

    private final String url;
    private final Properties properties;

    public JdbcSessionRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        properties = new Properties();

        InputStream in = getClass().getClassLoader().getResourceAsStream("/application.properties");
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException("application.properties not found in classpath", e);
        }

        url = properties.getProperty("url");

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(SESSIONS_TABLE_CREATE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating sessions table or connecting to database", e);
        }
    }

    @Override
    public void addSession(UUID sessionId, UUID userId, LocalDateTime expiresAt) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(ADD_NEW_SESSION_QUERY)) {
            preparedStatement.setObject(1, sessionId, Types.OTHER);
            preparedStatement.setObject(2, userId, Types.OTHER);
            preparedStatement.setObject(3, expiresAt, Types.OTHER);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UUID getUserIdBySessionId(UUID sessionId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_ID_BY_SESSION_ID_QUERY)) {
            preparedStatement.setObject(1, sessionId, Types.OTHER);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                UUID userId = rs.getObject("user_id", UUID.class);
                rs.close();
                return userId;
            }
            throw new IllegalArgumentException("Session not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSession(UUID sessionId) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(REMOVE_SESSION_QUERY)) {
            preparedStatement.setObject(1, sessionId, Types.OTHER);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
