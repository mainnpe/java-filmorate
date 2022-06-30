package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.model.eventmanager.SysEvent;

import java.util.List;

@Service
public class EventManager<T extends SysEvent> {
    @Autowired
    private List<EventStorage> storages;

    public void register(T event){
        for(EventStorage s: storages)
        {
            if(event.getStorageName().equals(s.getClass().getSimpleName()))
            {
                s.createEvent(event);
                break;
            }
        }
    }

}
