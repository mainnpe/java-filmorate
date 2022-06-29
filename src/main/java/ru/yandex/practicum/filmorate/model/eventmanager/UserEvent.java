package ru.yandex.practicum.filmorate.model.eventmanager;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.interfaces.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserEventStorageImpl;

import javax.validation.constraints.Min;

@Data
@Validated
public class UserEvent extends SysEvent{
    @NonNull
    @Min(1)
    protected long userId;
    protected UserEventType eventType;
    protected UserOperation operation;

    public UserEvent(long userId, long entityId,
                     UserEventType eventType, UserOperation operation) {
        super(entityId);
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
    }

   @Override
   public String getStorageName(){
        return "UserEventStorageImpl";
    }
}
