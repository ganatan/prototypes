# springboot-gateway

## Principe

On part d'un projet Spring Boot existant :

```text
springboot-crud
```

Le projet contient déjà :

```text
pom.xml
src/main/java
src/main/resources
application.yml
```

On ajoute uniquement Spring Cloud Gateway et une route locale de test.

---

## pom.xml

Ajouter dans `<properties>` :

```xml
<spring-cloud.version>2025.1.3</spring-cloud.version>
```

Ajouter dans `<dependencies>` :

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
</dependency>
```

Ajouter après `<dependencies>` :

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
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

Si ton fichier contient déjà :

```yaml
spring:
  application:
    name: springboot-crud
```

fusionner simplement les propriétés :

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

Ajouter un contrôleur de test :

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

Adapter uniquement le package à celui déjà utilisé dans ton projet.

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

## Test via Gateway

```text
http://localhost:3000/api/hello
```

La Gateway transforme :

```text
/api/hello
    ↓
/hello
```

et appelle :

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

Tout fonctionne dans le même projet :

```text
springboot-crud
```

avec :

```text
1 application
1 JVM
1 port
0 Docker
0 Eureka
0 autre microservice
```

