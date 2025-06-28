package log.model;

import jakarta.validation.constraints.NotNull;
import log.model.enums.LogLevel;
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

public class SystemLogDto {
    @NotNull
    private String service;

    @NotNull
    private LogLevel level;

    private String exception;

    private String stackTrace;

    @NotNull
    private LocalDateTime timestamp;

    private String message;
}
