package com.ganatan.starter.api.kafka;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

  private final KafkaService kafkaService;

  public KafkaController(KafkaService kafkaService) {
    this.kafkaService = kafkaService;
  }

  public record KafkaMessage(String message) {
  }

  @GetMapping
  public Map<String, String> status() {
    Map<String, String> test;
    test = Map.of(
        "status", "running",
        "topic", "media"
    );
    return test;
  }

  @PostMapping("/send")
  public Map<String, String> send(@RequestBody KafkaMessage body) {
    kafkaService.send(body.message());

    return Map.of(
        "status", "sent",
        "message", body.message()
    );
  }

  @GetMapping("/messages")
  public List<String> messages() {
    return kafkaService.messages();
  }
}