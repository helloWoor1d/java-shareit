package shareit.logging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import log.model.BusinessLogDto;
import log.model.SecurityLogDto;
import log.model.SystemLogDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<Integer, BusinessLogDto> businessLogConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "shareit-group");

        JsonDeserializer<BusinessLogDto> jsonDeserialize =
                new JsonDeserializer<>(BusinessLogDto.class, objectMapper, false);

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new IntegerDeserializer(),
                jsonDeserialize);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, BusinessLogDto> businessLogConcurrentKafkaListenerContainerFactory(ConsumerFactory<Integer, BusinessLogDto> consumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<Integer, BusinessLogDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }

    @Bean
    public ConsumerFactory<Integer, SecurityLogDto> securityLogConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "shareit-group");

        JsonDeserializer<SecurityLogDto> jsonDeserialize =
                new JsonDeserializer<>(SecurityLogDto.class, objectMapper, false);

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new IntegerDeserializer(),
                jsonDeserialize);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, SecurityLogDto> securityLogConcurrentKafkaListenerContainerFactory(ConsumerFactory<Integer, SecurityLogDto> consumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<Integer, SecurityLogDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }

    @Bean
    public ConsumerFactory<Integer, SystemLogDto> systemLogConsumerFactory(ObjectMapper objectMapper) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "shareit-group");

        JsonDeserializer<SystemLogDto> jsonDeserialize =
                new JsonDeserializer<>(objectMapper);

        return new DefaultKafkaConsumerFactory<>(
                properties,
                new IntegerDeserializer(),
                jsonDeserialize);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, SystemLogDto> systemLogConcurrentKafkaListenerContainerFactory(ConsumerFactory<Integer, SystemLogDto> consumerFactory) {
        var containerFactory = new ConcurrentKafkaListenerContainerFactory<Integer, SystemLogDto>();
        containerFactory.setConcurrency(1);
        containerFactory.setConsumerFactory(consumerFactory);
        return containerFactory;
    }
}
