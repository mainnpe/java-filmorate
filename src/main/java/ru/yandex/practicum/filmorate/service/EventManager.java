package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.model.eventmanager.SysEvent;

@Component
public class EventManager<T extends SysEvent> {

    private static EventManager instance;

    public static synchronized EventManager get() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    public void register(EventStorage storage, T event){
        storage.createEvent(event);
    }

}
