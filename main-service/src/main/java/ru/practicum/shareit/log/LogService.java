package ru.practicum.shareit.log;

import log.model.BusinessLogDto;
import log.model.SecurityLogDto;
import log.model.SystemLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {
    private final KafkaTemplate<Integer, SecurityLogDto> kafkaTemplateSecurity;
    private final KafkaTemplate<Integer, BusinessLogDto> kafkaTemplateBusiness;
    private final KafkaTemplate<Integer, SystemLogDto> kafkaTemplateSystem;

    public void send(SecurityLogDto log) {
        kafkaTemplateSecurity.send("security_logs", 0, log);
    }

    public void send(BusinessLogDto log) {
        kafkaTemplateBusiness.send("business_logs", 0, log);
    }

    public void send(SystemLogDto log) {
        kafkaTemplateSystem.send("system_logs", 0, log);
    }
}
