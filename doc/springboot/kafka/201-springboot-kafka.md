# Projet springboot-kafka

## Dépendances Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-kafka</artifactId>
</dependency>
```

## application.properties

```properties
server.port=3000

spring.application.name=springboot-starter

spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.consumer.group-id=ganatan-group
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

## application.yml

```yaml
server:
  port: 3000

spring:
  application:
    name: springboot-starter

  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: ganatan-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
```

## KafkaConfig.java

```java
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
```

## KafkaService.java

```java
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
```

## KafkaController.java

```java
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

  public record KafkaMessage(String message) {}

  @GetMapping
  public Map<String, String> status() {
    return Map.of(
        "status", "running",
        "topic", "media"
    );
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
```

## Docker Compose

```yaml
services:
  kafka:
    image: confluentinc/cp-kafka:7.6.1
    container_name: kafka-starter
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      CLUSTER_ID: MkU3OEVBNTcwNTJENDM2Qk
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://0.0.0.0:9092,CONTROLLER://kafka:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-starter-ui
    ports:
      - "8085:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
    depends_on:
      - kafka
```

## Exécution

```bash
docker compose up -d
mvn clean spring-boot:run
```

## Vérifier l'API

```text
GET http://localhost:3000/kafka
```

Résultat :

```json
{
  "status": "running",
  "topic": "media"
}
```

## Envoyer un message

```text
POST http://localhost:3000/kafka/send
```

Body :

```json
{
  "message": "Interstellar"
}
```

Réponse :

```json
{
  "status": "sent",
  "message": "Interstellar"
}
```

Avec curl :

```bash
curl -X POST http://localhost:3000/kafka/send -H "Content-Type: application/json" -d "{\"message\":\"Interstellar\"}"
```

Envoyer d'autres messages :

```json
{
  "message": "Dune"
}
```

```json
{
  "message": "Alien"
}
```

## Lire les messages

```text
GET http://localhost:3000/kafka/messages
```

Résultat :

```json
[
  "Interstellar",
  "Dune",
  "Alien"
]
```
