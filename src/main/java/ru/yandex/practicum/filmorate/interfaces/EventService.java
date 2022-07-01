package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.eventmanager.SysEvent;

import java.util.Collection;

public interface EventService<T extends SysEvent> {
    Collection<T> getUserEventsById(long userId);
}
