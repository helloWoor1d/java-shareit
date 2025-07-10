package log.model;

import jakarta.validation.constraints.NotNull;
import log.model.enums.LogEvent;
import log.model.enums.LogLevel;
import log.model.enums.LogType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString

public class BusinessLogDto {
    @NotNull
    private String service;

    @NotNull
    private LogLevel level;

    @NotNull
    private LogType type;

    private LogEvent event;

    private Long userId;

    private String entityType;

    private Long entityId;

    private String action;

    @NotNull
    private LocalDateTime timestamp;

    private String message;
}
