package ru.itis.spotty.repositories;

import ru.itis.spotty.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class JdbcUserRepository implements UserRepository {

    private final String USERS_TABLE_CREATE_QUERY = """
            CREATE TABLE IF NOT EXISTS users (
                user_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                username VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(255) NOT NULL UNIQUE,
                avatar_url VARCHAR(500),
                bio TEXT,
                password_hash VARCHAR(255) NOT NULL,
                salt VARCHAR(64) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            );
            """;

    private final String CREATE_USER_QUERY = """
            INSERT INTO users (username, email, avatar_url, bio, password_hash, salt)
            VALUES (?, ?, ?, ?, ?, ?)
            RETURNING user_id;
    """;

    private final String GET_USER_BY_ID_QUERY = """
            SELECT * FROM users WHERE user_id = ?;
    """;

    private final String GET_USER_BY_USERNAME_QUERY = """
            SELECT * FROM users WHERE username = ?;
    """;

    private final String GET_USER_BY_EMAIL_QUERY = """
            SELECT * FROM users WHERE email = ?;
    """;

    private final String CHECK_USERNAME_QUERY = """
            SELECT COUNT(*) FROM users WHERE username = ?;
    """;

    private final String CHECK_EMAIL_QUERY = """
            SELECT COUNT(*) FROM users WHERE email = ?;
    """;

    private final String UPDATE_USER_QUERY = """
            UPDATE users
            SET username = ?, email = ?, avatar_url = ?, bio = ?
            WHERE user_id = ?;
    """;

    private final String FIND_ELIGIBLE_USERS_QUERY = """
            SELECT * FROM users WHERE username ILIKE ?;
    """;

    private final Properties properties;
    private final String url;

    public JdbcUserRepository() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        properties = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("/application.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("application.properties not found in classpath", e);
        }

        url = properties.getProperty("url");
        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(USERS_TABLE_CREATE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating posts table or connecting to database", e);
        }
    }

    @Override
    public UUID createUser(User user) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_USER_QUERY)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getAvatarUrl());
            preparedStatement.setString(4, user.getBio());
            preparedStatement.setString(5, user.getPasswordHash());
            preparedStatement.setString(6, user.getSalt());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                UUID id = resultSet.getObject("user_id", UUID.class);
                resultSet.close();
                user.setUserId(id);
                return id;
            }

            throw new IllegalArgumentException("User id not created");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserById(UUID id) {
        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_ID_QUERY)) {
            preparedStatement.setObject(1, id, Types.OTHER);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = resultSetToUser(resultSet);
                resultSet.close();
                return user;
            }

            throw new IllegalArgumentException("User not found");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_USERNAME_QUERY)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = resultSetToUser(resultSet);
                resultSet.close();
                return user;
            }

            throw new IllegalArgumentException("User not found");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement preparedStatement = connection.prepareStatement(GET_USER_BY_EMAIL_QUERY)) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = resultSetToUser(resultSet);
                resultSet.close();
                return user;
            }

            throw new IllegalArgumentException("User not found");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isEmailExists(String email) {
        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement preparedStatement = connection.prepareStatement(CHECK_EMAIL_QUERY)) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isUsernameExists(String username) {
        try (Connection connection = DriverManager.getConnection(url, properties);
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_USERNAME_QUERY)) {
            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                resultSet.close();
                return count > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateUser(User user) {
        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER_QUERY)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getAvatarUrl());
            preparedStatement.setString(4, user.getBio());
            preparedStatement.setObject(5, user.getUserId(),  Types.OTHER);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> findEligibleUsers(String query) {
        List<User> users = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, properties);
        PreparedStatement preparedStatement = connection.prepareStatement(FIND_ELIGIBLE_USERS_QUERY)) {
            String preparedQuery = "%" + query + "%";
            preparedStatement.setString(1, preparedQuery);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = resultSetToUser(resultSet);
                users.add(user);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    private User resultSetToUser(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("user_id", UUID.class);
        String username = rs.getString("username");
        String email = rs.getString("email");
        String avatarUrl = rs.getString("avatar_url");
        String bio = rs.getString("bio");
        String passwordHash = rs.getString("password_hash");
        String salt = rs.getString("salt");
        Timestamp createdAt = rs.getTimestamp("created_at");

        User user = new User(username, email, avatarUrl, bio, passwordHash, salt, createdAt.toLocalDateTime());
        user.setUserId(id);
        return user;
    }
}
