package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.model.eventmanager.SysEvent;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEvent;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEventType;
import ru.yandex.practicum.filmorate.model.eventmanager.UserOperation;
import java.util.*;

@Component
@Slf4j
@Qualifier("userEventStorage")
public class UserEventStorageImpl implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String SELECT_EVENTS_BY_USER_ID = "SELECT tb1.* " +
            "FROM user_events AS tb1 " +
            "WHERE tb1.user_id = ? ";

    private final String INSERT_USER_EVENT = "INSERT INTO user_events " +
            "(user_id, entity_id, event_type, user_operation) VALUES (?, ?, ?, ?)";

    public UserEventStorageImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<UserEvent> findEventsByUserId(long userId) {
        List<UserEvent> result = new ArrayList<>();
        SqlRowSet rows = jdbcTemplate.queryForRowSet(
                SELECT_EVENTS_BY_USER_ID, userId);
        while (rows.next()) {
            result.add(buildUserEvent(rows));
        }
        return result;
    }

    @Override
    public void createEvent(SysEvent event) {
        UserEvent e = (UserEvent) event;
        jdbcTemplate.update(INSERT_USER_EVENT,
                e.getUserId(),
                e.getEntityId(),
                e.getEventType().name(),
                e.getOperation().name());
    }

    private UserEvent buildUserEvent(SqlRowSet rows){
        UserEvent event = new UserEvent();
        event.setEventId(rows.getLong("event_id"));
        event.setEntityId(rows.getLong("entity_id"));
        event.setCdate(rows.getTimestamp("cdate"));
        event.setUserId(rows.getLong("user_id"));
        event.setEventType(UserEventType.valueOf(
                rows.getString("event_type")
        ));
        event.setOperation(UserOperation.valueOf(
                rows.getString("user_operation")
        ));
        return event;

    }
}