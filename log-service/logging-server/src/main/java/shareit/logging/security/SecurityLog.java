package shareit.logging.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@ToString(callSuper = true)

@Entity
@Table(name = "security_logs")
public class SecurityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String service;

    @Enumerated(EnumType.STRING)
    private LogLevel level;

    @Enumerated(EnumType.STRING)
    private LogType type;

    @Enumerated(EnumType.STRING)
    private LogEvent event;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_ip")
    private String userIp;

    @Column(name = "auth_method")
    private String authMethod;

    private LocalDateTime timestamp;

    private String message;
}
