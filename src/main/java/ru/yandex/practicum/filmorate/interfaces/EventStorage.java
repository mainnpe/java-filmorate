package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.eventmanager.UserEventType;
import ru.yandex.practicum.filmorate.model.eventmanager.SysEvent;

import java.util.Collection;

public interface EventStorage<T extends SysEvent> {
    Collection<T> findEventsByUserId(long id);

    void createEvent(T event);
}
