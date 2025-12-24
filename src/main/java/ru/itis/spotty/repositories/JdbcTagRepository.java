package ru.itis.spotty.repositories;

import ru.itis.spotty.models.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class JdbcTagRepository implements TagRepository {

    private final String CREATE_TAGS_TABLE_QUERY = """
                CREATE TABLE IF NOT EXISTS tags (
                    tag_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                    tag_name varchar(30),
                    category varchar(50)
                );
    """;

    private final String GET_ALL_TAGS_QUERY = """
                SELECT * FROM tags;
    """;


    private Properties properties;
    private String url;

    public JdbcTagRepository() {
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

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_TAGS_TABLE_QUERY);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating posts table or connecting to database", e);
        }
    }

    @Override
    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, properties);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(GET_ALL_TAGS_QUERY);

            Tag tag;
            while (resultSet.next()) {
                tag = toTag(resultSet);
                tags.add(tag);
            }

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return tags;
    }

    private Tag toTag(ResultSet resultSet) throws SQLException {
        UUID id = UUID.fromString(resultSet.getString("tag_id"));
        String name = resultSet.getString("tag_name");
        String category = resultSet.getString("category");
        return new Tag(id, name, category);
    }
}
