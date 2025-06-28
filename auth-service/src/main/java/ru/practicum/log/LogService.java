package ru.practicum.log;

import log.model.SecurityLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final KafkaTemplate<Integer, SecurityLogDto> kafkaTemplate;

    public void send(SecurityLogDto log) {
        kafkaTemplate.send("security_logs", 0, log);
    }
}
