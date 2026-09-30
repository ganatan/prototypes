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

On ajoute donc la version MVC de Spring Cloud Gateway.

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

Ajouter la route Gateway :

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
```

---

## RootController

Le projet possède déjà :

```text
src/main/java/com/ganatan/starter/api/root/RootController.java
```

Aucune modification n'est nécessaire si le contrôleur expose déjà :

```text
/
```

Exemple :

```java
package com.ganatan.starter.api.root;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
            "application", "springboot-starter",
            "status", "running"
        );
    }
}
```

---

## Run

```bash
mvn spring-boot:run
```

---

## Test direct

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

## Test Gateway

```text
http://localhost:3000/api/root
```

La Gateway transforme :

```text
/api/root
↓
/
```

puis appelle :

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

## Fonctionnement

```text
Client
  |
  | GET /api/root
  v
Spring Cloud Gateway WebMVC
  |
  | RewritePath
  v
GET /
  |
  v
RootController
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