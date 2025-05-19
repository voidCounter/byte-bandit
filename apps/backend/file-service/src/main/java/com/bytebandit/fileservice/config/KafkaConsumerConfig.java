package com.bytebandit.fileservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

@Configuration
public class KafkaConsumerConfig {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    
    /**
     * Creates a KafkaListenerErrorHandler bean to handle errors during message processing.
     *
     * @return the KafkaListenerErrorHandler instance
     */
    @Bean
    public KafkaListenerErrorHandler kafkaErrorHandler() {
        return (message, exception) -> {
            logger.error("ERROR PROCESSING KAFKA MESSAGE: {}", exception.getMessage(), exception);
            return null;
        };
    }
}
