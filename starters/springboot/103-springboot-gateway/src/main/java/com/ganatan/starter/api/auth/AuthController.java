package com.ganatan.starter.api.auth;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

  @PostMapping("/login")
  public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
    String username = body.get("username");
    String password = body.get("password");

    if ("admin".equals(username) && "admin".equals(password)) {
      return ResponseEntity.ok(Map.of("token", "ganatan-token"));
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized"));
  }
}