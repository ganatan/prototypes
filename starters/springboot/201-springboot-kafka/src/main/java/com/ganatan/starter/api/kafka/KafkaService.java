package com.ganatan.starter.api.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final List<String> messages = new CopyOnWriteArrayList<>();

  public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void send(String message) {
    kafkaTemplate.send("media", message);
  }

  @KafkaListener(topics = "media")
  public void receive(String message) {
    messages.add(message);
  }

  public List<String> messages() {
    return new ArrayList<>(messages);
  }
}