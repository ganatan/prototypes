# springboot-gateway

## Principe

On part d'un projet Spring Boot existant :

```text
springboot-crud
```

On ajoute uniquement Spring Cloud Gateway.

---

## pom.xml

Ajouter dans `<dependencies>` :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
    <version>5.0.3</version>
</dependency>
```

Version utilisée :

```text
Spring Cloud Gateway 5.0.3
```

---

## application.yml

Ajouter :

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: hello
              uri: http://localhost:3000
              predicates:
                - Path=/api/hello
              filters:
                - RewritePath=/api/hello, /hello
```

Si le fichier contient déjà :

```yaml
spring:
  application:
    name: springboot-crud
```

utiliser :

```yaml
spring:
  application:
    name: springboot-crud
  cloud:
    gateway:
      server:
        webflux:
          routes:
            - id: hello
              uri: http://localhost:3000
              predicates:
                - Path=/api/hello
              filters:
                - RewritePath=/api/hello, /hello
```

---

## Controller

Ajouter :

```text
HelloController.java
```

```java
package com.ganatan.springbootcrud;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "application", "springboot-crud",
            "message", "Hello from Gateway"
        );
    }
}
```

Adapter le package si nécessaire.

---

## Run

```bash
mvn spring-boot:run
```

---

## Test direct

```text
http://localhost:3000/hello
```

Réponse :

```json
{
  "application": "springboot-crud",
  "message": "Hello from Gateway"
}
```

---

## Test Gateway

```text
http://localhost:3000/api/hello
```

La Gateway transforme :

```text
/api/hello
↓
/hello
```

Réponse :

```json
{
  "application": "springboot-crud",
  "message": "Hello from Gateway"
}
```

---

## Fonctionnement

```text
Client
  |
  | GET /api/hello
  v
Spring Cloud Gateway
  |
  | RewritePath
  v
GET /hello
  |
  v
HelloController
```

Tout fonctionne localement avec :

```text
1 projet
1 JVM
1 port
0 Docker
0 Eureka
0 autre service
```