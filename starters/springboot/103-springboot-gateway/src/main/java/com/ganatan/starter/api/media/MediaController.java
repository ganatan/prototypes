package com.ganatan.starter.api.media;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {

  @GetMapping("/medias")
  public List<Map<String, Object>> medias() {
    return List.of(
        Map.of("id", 1, "name", "Alien", "year", 1979, "type", "movie"),
        Map.of("id", 2, "name", "Interstellar", "year", 2014, "type", "movie"),
        Map.of("id", 3, "name", "The Last of Us", "year", 2023, "type", "series")
    );
  }
}