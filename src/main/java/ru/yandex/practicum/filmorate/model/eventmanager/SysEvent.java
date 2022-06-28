package ru.yandex.practicum.filmorate.model.eventmanager;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public abstract class SysEvent {
    private long eventId;
    private long entityId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Timestamp cdate = Timestamp.from(Instant.now());

    public SysEvent(long entityId) {
        this.entityId = entityId;
    }
}
