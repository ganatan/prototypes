# springboot-gateway


## Structure

```text
springboot-gateway
├── pom.xml
└── src
    └── main
        ├── java
        │   └── com
        │       └── ganatan
        │           └── springbootgateway
        │               ├── SpringbootGatewayApplication.java
        │               └── HelloController.java
        └── resources
            └── application.yml
```

---

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.1.1</version>
        <relativePath/>
    </parent>

    <groupId>com.ganatan</groupId>
    <artifactId>springboot-gateway</artifactId>
    <version>1.0.0</version>
    <name>springboot-gateway</name>

    <properties>
        <java.version>21</java.version>
        <spring-cloud.version>2025.1.3</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway-server-webflux</artifactId>
        </dependency>
    </dependencies>

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

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

---

## SpringbootGatewayApplication.java

```java
package com.ganatan.springbootgateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootGatewayApplication.class, args);
    }
}
```

---

## HelloController.java

```java
package com.ganatan.springbootgateway;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of(
            "application", "springboot-gateway",
            "message", "Hello from Spring Cloud Gateway"
        );
    }
}
```

---

## application.yml

```yaml
server:
  port: 3000

spring:
  application:
    name: springboot-gateway
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

## Build

```bash
mvn clean install
```

Artefact généré :

```text
target/springboot-gateway-1.0.0.jar
```

---

## Run

### Exécution via Maven

```bash
mvn spring-boot:run
```

### Exécution via Java

```bash
java -jar target/springboot-gateway-1.0.0.jar
```

---

## Test direct

Appeler directement le contrôleur :

```text
http://localhost:3000/hello
```

Réponse :

```json
{
  "application": "springboot-gateway",
  "message": "Hello from Spring Cloud Gateway"
}
```

---

## Test via la Gateway

Appeler :

```text
http://localhost:3000/api/hello
```

La Gateway applique la route :

```text
/api/hello
    ↓
Spring Cloud Gateway
    ↓
/hello
    ↓
HelloController
```

Réponse :

```json
{
  "application": "springboot-gateway",
  "message": "Hello from Spring Cloud Gateway"
}
```

---

## Principe

La route :

```yaml
- id: hello
  uri: http://localhost:3000
  predicates:
    - Path=/api/hello
  filters:
    - RewritePath=/api/hello, /hello
```

signifie :

```text
Client
  |
  | GET /api/hello
  v
Spring Cloud Gateway
  |
  | GET /hello
  v
HelloController
```

Pour ce premier exemple, la Gateway et l'API cible sont dans la même application.

Cela permet de tester Spring Cloud Gateway avec :

```text
1 projet
1 JVM
1 port
0 Docker
0 Kafka
0 Eureka
0 microservice supplémentaire
```

---

## Évolution vers plusieurs services

Plus tard, il suffira de remplacer :

```yaml
uri: http://localhost:3000
```

par l'adresse d'un vrai service :

```yaml
uri: http://localhost:3001
```

Par exemple :

```text
http://localhost:3000/api/movies
                  |
                  v
          springboot-gateway
                  |
                  v
http://localhost:3001/movies
```

La Gateway devient alors le point d'entrée unique de plusieurs API.