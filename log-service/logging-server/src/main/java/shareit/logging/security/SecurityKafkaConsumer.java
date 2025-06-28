package shareit.logging.security;

import log.model.SecurityLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityKafkaConsumer {
    private final SecurityRepository securityRepository;

    @KafkaListener(topics = "security_logs", groupId = "shareit-group", containerFactory = "securityLogConcurrentKafkaListenerContainerFactory")
    public void consume(SecurityLogDto logDto) {
        SecurityLog securityLog = map(logDto);
        securityRepository.save(securityLog);
    }

    private SecurityLog map(SecurityLogDto logDto) {
        return SecurityLog.builder()
                .service(logDto.getService())
                .level(logDto.getLevel())
                .type(logDto.getType())
                .event(logDto.getEvent())
                .message(logDto.getMessage())
                .timestamp(logDto.getTimestamp())
                .authMethod(logDto.getAuthMethod())
                .userIp(logDto.getUserIp())
                .userId(logDto.getUserId())
                .build();
    }
}
