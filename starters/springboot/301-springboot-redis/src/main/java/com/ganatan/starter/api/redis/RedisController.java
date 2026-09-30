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