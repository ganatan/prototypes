# Projet springboot-redis

## Dépendances Maven

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

## application.properties

```properties
server.port=3000

spring.application.name=springboot-redis

spring.data.redis.host=localhost
spring.data.redis.port=6379
```

## application.yml

```yaml
server:
  port: 3000

spring:
  application:
    name: springboot-redis

  data:
    redis:
      host: localhost
      port: 6379
```

## RedisService.java

```java
package com.ganatan.starter.api.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

  private final StringRedisTemplate redisTemplate;

  public RedisService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void set(String key, String value) {
    redisTemplate.opsForValue().set(key, value);
  }

  public String get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public Boolean delete(String key) {
    return redisTemplate.delete(key);
  }
}
```

## RedisController.java

```java
package com.ganatan.starter.api.redis;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisController {

  private final RedisService redisService;

  public RedisController(RedisService redisService) {
    this.redisService = redisService;
  }

  public record RedisMessage(String key, String value) {}

  @GetMapping
  public Map<String, String> status() {
    return Map.of(
        "status", "running",
        "database", "redis"
    );
  }

  @PostMapping
  public Map<String, String> set(@RequestBody RedisMessage body) {
    redisService.set(body.key(), body.value());

    return Map.of(
        "status", "saved",
        "key", body.key(),
        "value", body.value()
    );
  }

  @GetMapping("/{key}")
  public ResponseEntity<Map<String, String>> get(@PathVariable String key) {
    String value = redisService.get(key);

    if (value == null) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok(Map.of(
        "key", key,
        "value", value
    ));
  }

  @DeleteMapping("/{key}")
  public Map<String, Object> delete(@PathVariable String key) {
    Boolean deleted = redisService.delete(key);

    return Map.of(
        "status", "deleted",
        "key", key,
        "deleted", deleted
    );
  }
}
```

## Docker Compose

```yaml
services:
  redis:
    image: redis:latest
    container_name: redis-starter
    ports:
      - "6379:6379"

  redis-ui:
    image: redis/redisinsight:latest
    container_name: redis-starter-ui
    ports:
      - "5540:5540"
    depends_on:
      - redis
```

## Exécution

```bash
docker compose up -d
mvn clean spring-boot:run
```

## Vérifier Redis

```bash
docker compose ps
```

Tester directement Redis :

```bash
docker exec -it redis-starter redis-cli
```

Puis :

```text
PING
```

Résultat :

```text
PONG
```

## Vérifier l'API

```text
GET http://localhost:3000/redis
```

Résultat :

```json
{
  "database": "redis",
  "status": "running"
}
```

## Enregistrer une valeur

```text
POST http://localhost:3000/redis
```

Body :

```json
{
  "key": "movie:1",
  "value": "Interstellar"
}
```

Réponse :

```json
{
  "status": "saved",
  "key": "movie:1",
  "value": "Interstellar"
}
```

Cela correspond à la commande Redis :

```text
SET movie:1 Interstellar
```

## Lire une valeur

```text
GET http://localhost:3000/redis/movie:1
```

Résultat :

```json
{
  "key": "movie:1",
  "value": "Interstellar"
}
```

Cela correspond à :

```text
GET movie:1
```

## Supprimer une valeur

```text
DELETE http://localhost:3000/redis/movie:1
```

Résultat :

```json
{
  "status": "deleted",
  "key": "movie:1",
  "deleted": true
}
```

Cela correspond à :

```text
DEL movie:1
```

## Vérifier directement avec redis-cli

```bash
docker exec -it redis-starter redis-cli
```

Créer :

```text
SET movie:1 Interstellar
```

Lire :

```text
GET movie:1
```

Résultat :

```text
"Interstellar"
```

Supprimer :

```text
DEL movie:1
```

## Redis Insight

```text
http://localhost:5540
```

Redis Insight permet de visualiser les clés et les valeurs présentes dans Redis.

## Principe

```text
POST /redis
    ↓
StringRedisTemplate
    ↓
SET
    ↓
Redis
```

```text
GET /redis/movie:1
    ↓
StringRedisTemplate
    ↓
GET
    ↓
Interstellar
```

```text
DELETE /redis/movie:1
    ↓
StringRedisTemplate
    ↓
DEL
    ↓
Redis
```

## Commandes Redis utilisées

```text
SET
GET
DEL
```