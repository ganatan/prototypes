## pom.xml

```xml
<dependency>
  <groupId>org.springframework.kafka</groupId>
  <artifactId>spring-kafka</artifactId>
</dependency>
```

## application.properties

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=springboot-starter
spring.kafka.consumer.auto-offset-reset=earliest
```

## KafkaController.java

```java
package com.ganatan.starter.api.kafka;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
public class KafkaController {

  private final KafkaAdmin kafkaAdmin;
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ConsumerFactory<String, String> consumerFactory;

  public KafkaController(KafkaAdmin kafkaAdmin, KafkaTemplate<String, String> kafkaTemplate, ConsumerFactory<String, String> consumerFactory) {
    this.kafkaAdmin = kafkaAdmin;
    this.kafkaTemplate = kafkaTemplate;
    this.consumerFactory = consumerFactory;
  }

  @GetMapping
  public Map<String, Object> status() throws Exception {
    try (Admin admin = Admin.create(kafkaAdmin.getConfigurationProperties())) {
      return Map.of("status", "connected", "topics", admin.listTopics().names().get());
    }
  }

  @PostMapping("/{topic}")
  public Map<String, String> create(@PathVariable String topic) throws Exception {
    try (Admin admin = Admin.create(kafkaAdmin.getConfigurationProperties())) {
      admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1))).all().get();
      return Map.of("topic", topic, "status", "created");
    }
  }

  @PostMapping("/{topic}/send")
  public Map<String, String> send(@PathVariable String topic, @RequestParam String message) throws Exception {
    kafkaTemplate.send(topic, message).get();
    return Map.of("topic", topic, "message", message);
  }

  @GetMapping("/{topic}")
  public List<String> read(@PathVariable String topic) {
    try (Consumer<String, String> consumer = consumerFactory.createConsumer()) {
      List<TopicPartition> partitions = consumer.partitionsFor(topic).stream()
          .map(partition -> new TopicPartition(topic, partition.partition()))
          .toList();

      consumer.assign(partitions);
      consumer.seekToBeginning(partitions);

      return consumer.poll(Duration.ofSeconds(2)).records(topic).stream()
          .map(record -> record.value())
          .toList();
    }
  }
}
```

## Tester Kafka

```text
http://localhost:3000/kafka
```

Résultat :

```json
{
  "status": "connected",
  "topics": []
}
```

## Créer un topic

```bash
curl -X POST http://localhost:3000/kafka/media
```

Résultat :

```json
{
  "topic": "media",
  "status": "created"
}
```

## Envoyer des messages

```bash
curl -X POST "http://localhost:3000/kafka/media/send?message=Interstellar"
curl -X POST "http://localhost:3000/kafka/media/send?message=Dune"
curl -X POST "http://localhost:3000/kafka/media/send?message=Alien"
```

## Lire les messages

```text
http://localhost:3000/kafka/media
```

Résultat :

```json
[
  "Interstellar",
  "Dune",
  "Alien"
]
```

## Kafka UI

```text
http://localhost:8085
```