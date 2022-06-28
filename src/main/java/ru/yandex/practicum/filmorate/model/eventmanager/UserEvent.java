package ru.yandex.practicum.filmorate.model.eventmanager;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import java.sql.Timestamp;

@Data
@Validated
@NoArgsConstructor
public class UserEvent extends SysEvent{
    @NonNull
    @Min(1)
    private long userId;
    private UserEventType eventType;
    private UserOperation operation;

    public UserEvent(long userId, long entityId,
                     UserEventType eventType, UserOperation operation) {
        super(entityId);
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
    }
}
