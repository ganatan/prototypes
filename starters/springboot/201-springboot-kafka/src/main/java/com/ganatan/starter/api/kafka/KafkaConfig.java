package com.ganatan.starter.api.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

  @Bean
  public NewTopic mediaTopic() {
    return new NewTopic("media", 1, (short) 1);
  }
}