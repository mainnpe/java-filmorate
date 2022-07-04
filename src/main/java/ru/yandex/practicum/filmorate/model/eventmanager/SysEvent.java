package ru.yandex.practicum.filmorate.model.eventmanager;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@Validated
public abstract class SysEvent {
    protected long eventId;
    protected long entityId;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("timestamp")
    protected Timestamp cdate = Timestamp.from(Instant.now());

    public SysEvent(long entityId) {
        this.entityId = entityId;
    }

    public String getStorageName(){
        return "";
    }

}
