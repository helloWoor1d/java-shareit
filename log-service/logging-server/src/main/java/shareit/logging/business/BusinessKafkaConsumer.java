package shareit.logging.business;

import log.model.BusinessLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessKafkaConsumer {
    private final BusinessRepository businessRepository;

    @KafkaListener(topics = "business_logs", groupId = "shareit-group", containerFactory = "businessLogConcurrentKafkaListenerContainerFactory")
    public void consume(BusinessLogDto logDto) {
        BusinessLog log = map(logDto);
        businessRepository.save(log);
    }

    private BusinessLog map(BusinessLogDto logDto) {
        return BusinessLog.builder()
                .service(logDto.getService())
                .level(logDto.getLevel())
                .type(logDto.getType())
                .event(logDto.getEvent())
                .action(logDto.getAction())
                .userId(logDto.getUserId())
                .entityType(logDto.getEntityType())
                .entityId(logDto.getEntityId())
                .message(logDto.getMessage())
                .timestamp(logDto.getTimestamp())
                .build();
    }
}
