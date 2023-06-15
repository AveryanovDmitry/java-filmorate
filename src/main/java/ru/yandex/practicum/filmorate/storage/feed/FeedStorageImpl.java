package ru.yandex.practicum.filmorate.storage.feed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Component
public class FeedStorageImpl implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFeedList(int userId, int entityId, EventType type, Operation operation) {
        String sqlQuery = "INSERT INTO feed (entity_id, operation_name, event_type, user_id, timestamp)" +
                "VALUES (?, ?, ?, ?, ?) ";
        Feed feed = new Feed();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            ps.setLong(1, entityId);
            ps.setString(2, String.valueOf(operation));
            ps.setString(3, String.valueOf(type));
            ps.setInt(4, userId);
            Timestamp timestamp = Timestamp.from(Instant.now());
            ps.setLong(5, timestamp.getTime());
            return ps;
        }, keyHolder);
        feed.setEventId(keyHolder.getKey().intValue());
    }

    public List<Feed> getFeedList(int id) {
        return jdbcTemplate.query("SELECT * FROM feed WHERE user_id = ?", this::feedMapper, id);
    }

    private Feed feedMapper(ResultSet resultSet, int rowNum) throws SQLException {
        Feed feed = new Feed();
        feed.setEventId(resultSet.getInt("event_id"));
        feed.setEntityId(resultSet.getInt("entity_id"));
        feed.setOperation(Operation.valueOf(resultSet.getString("operation_name")));
        feed.setEventType(EventType.valueOf(resultSet.getString("event_type")));
        feed.setUserId(resultSet.getInt("user_id"));
        feed.setTimestamp(resultSet.getLong("timestamp"));
        return feed;
    }
}
