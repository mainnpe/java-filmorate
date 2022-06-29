package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.EventService;
import ru.yandex.practicum.filmorate.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.model.eventmanager.UserEvent;

import java.util.Collection;

@Service
public class UserEventManagerImpl implements EventService {

    private EventStorage eventStorage;

    @Autowired
    public UserEventManagerImpl(EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    @Override
    public Collection<UserEvent> getUserEventsById(long userId) {
        return eventStorage.findEventsByUserId(userId);
    }

}
