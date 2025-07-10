package ru.practicum.shareit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import log.model.BusinessLogDto;
import log.model.SecurityLogDto;
import log.model.SystemLogDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<Integer, BusinessLogDto> businessLogDtoProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        JsonSerializer<BusinessLogDto> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProperties,
                new IntegerSerializer(),
                jsonSerializer
        );
    }

    @Bean
    public ProducerFactory<Integer, SecurityLogDto> securityLogDtoProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        JsonSerializer<SecurityLogDto> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProperties,
                new IntegerSerializer(),
                jsonSerializer
        );
    }

    @Bean
    public ProducerFactory<Integer, SystemLogDto> systemLogDtoProducerFactory(ObjectMapper objectMapper) {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        JsonSerializer<SystemLogDto> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                configProperties,
                new IntegerSerializer(),
                jsonSerializer
        );
    }

    @Bean
    public KafkaTemplate<Integer, BusinessLogDto> businessKafkaTemplate(
            ProducerFactory<Integer, BusinessLogDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<Integer, SecurityLogDto> securityKafkaTemplate(
            ProducerFactory<Integer, SecurityLogDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<Integer, SystemLogDto> systemKafkaTemplate(
            ProducerFactory<Integer, SystemLogDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
