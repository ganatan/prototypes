# springboot-gateway

## Principe

On part du projet Spring Boot existant :

```text
springboot-crud
```

Le projet utilise déjà Spring MVC avec :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

On ajoute Spring Cloud Gateway WebMVC.

La Gateway va permettre de :

```text
Router les requêtes
Réécrire les URLs
Contrôler l'accès avec un token
```

Exemple :

```text
/api/root
→ /

/api/medias
→ /medias
```

---

## pom.xml

Ajouter dans `<dependencies>` :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-server-webmvc</artifactId>
    <version>5.0.3</version>
</dependency>
```

Version utilisée :

```text
Spring Cloud Gateway Server WebMVC 5.0.3
```

Ne pas utiliser :

```text
spring-cloud-starter-gateway-server-webflux
```

car le projet utilise déjà Spring MVC.

---

## application.yaml

Le projet contient déjà :

```yaml
server:
  port: 3000

spring:
  application:
    name: springboot-starter
```

Ajouter les routes Gateway :

```yaml
server:
  port: 3000

spring:
  application:
    name: springboot-starter
  cloud:
    gateway:
      server:
        webmvc:
          routes:
            - id: root
              uri: http://localhost:3000
              predicates:
                - Path=/api/root
              filters:
                - RewritePath=/api/root, /
            - id: medias
              uri: http://localhost:3000
              predicates:
                - Path=/api/medias
              filters:
                - RewritePath=/api/medias, /medias
```

Les routes configurées sont :

```text
/api/root
→ /

/api/medias
→ /medias
```

---

## MediaController

Créer :

```text
src/main/java/com/ganatan/starter/api/media/MediaController.java
```

```java
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
```

---

## AuthController

Créer :

```text
src/main/java/com/ganatan/starter/api/auth/AuthController.java
```

```java
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
```

Le login simulé utilise :

```text
username : admin
password : admin
```

et retourne :

```text
ganatan-token
```

---

## TokenFilter

Créer :

```text
src/main/java/com/ganatan/starter/security/TokenFilter.java
```

```java
package com.ganatan.starter.security;

import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenFilter extends OncePerRequestFilter {

    private static final String TOKEN = "Bearer ganatan-token";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/medias")) {
            String authorization = request.getHeader("Authorization");

            if (!TOKEN.equals(authorization)) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

Le filtre protège :

```text
/api/medias
```

Le header attendu est :

```text
Authorization: Bearer ganatan-token
```

---

## Structure

```text
src/main/java/com/ganatan/starter
├── api.auth
│   └── AuthController.java
├── api.media
│   └── MediaController.java
├── api.root
│   └── RootController.java
├── security
│   └── TokenFilter.java
└── StarterApplication.java
```

---

## Run

```bash
mvn clean spring-boot:run
```

---

## Test Root direct

```text
http://localhost:3000/
```

Réponse :

```json
{
  "application": "springboot-starter",
  "status": "running"
}
```

---

## Test Root Gateway

```text
http://localhost:3000/api/root
```

La Gateway transforme :

```text
/api/root
↓
/
```

Réponse :

```json
{
  "application": "springboot-starter",
  "status": "running"
}
```

---

## Test Login avec accès

```bash
curl -X POST http://localhost:3000/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"admin\"}"
```

Réponse :

```json
{
  "token": "ganatan-token"
}
```

---

## Test Login sans accès

```bash
curl -X POST http://localhost:3000/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"admin\",\"password\":\"wrong\"}"
```

Réponse :

```json
{
  "error": "Unauthorized"
}
```

Statut HTTP :

```text
401 Unauthorized
```

---

## Test Medias sans token

```bash
curl http://localhost:3000/api/medias
```

Réponse :

```json
{
  "error": "Unauthorized"
}
```

Statut HTTP :

```text
401 Unauthorized
```

---

## Test Medias avec mauvais token

```bash
curl http://localhost:3000/api/medias ^
  -H "Authorization: Bearer mauvais-token"
```

Réponse :

```json
{
  "error": "Unauthorized"
}
```

Statut HTTP :

```text
401 Unauthorized
```

---

## Test Medias avec token

```bash
curl http://localhost:3000/api/medias ^
  -H "Authorization: Bearer ganatan-token"
```

Réponse :

```json
[
  {
    "id": 1,
    "name": "Alien",
    "year": 1979,
    "type": "movie"
  },
  {
    "id": 2,
    "name": "Interstellar",
    "year": 2014,
    "type": "movie"
  },
  {
    "id": 3,
    "name": "The Last of Us",
    "year": 2023,
    "type": "series"
  }
]
```

---

## Fonctionnement

Accès sans token :

```text
Client
  |
  | GET /api/medias
  v
TokenFilter
  |
  X
401 Unauthorized
```

Accès avec token :

```text
Client
  |
  | GET /api/medias
  | Authorization: Bearer ganatan-token
  v
TokenFilter
  |
  v
Spring Cloud Gateway WebMVC
  |
  | RewritePath
  v
GET /medias
  |
  v
MediaController
  |
  v
JSON
```

---

## Rôle de la Gateway

La Gateway assure maintenant :

```text
Routage
/api/root → /
/api/medias → /medias

Contrôle d'accès
/api/medias nécessite un token

Point d'entrée unique
http://localhost:3000
```

Le token utilisé ici est volontairement simulé :

```text
ganatan-token
```

