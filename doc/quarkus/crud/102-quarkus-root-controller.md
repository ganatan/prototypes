# RootController

```text
GET /
GET /info
GET /status
```
---

## Configuration Quarkus

### application.properties

Chemin :

```text
src/main/resources/application.properties
```

Configuration :

```properties
quarkus.application.name=quarkus-starter
quarkus.http.port=3000
```

L'application sera disponible sur :

```text
http://localhost:3000
```

---

## Ressource générée

Supprimer la ressource générée lors de l'initialisation :

```text
src/main/java/com/ganatan/starter/GreetingResource.java
```

La route :

```text
http://localhost:8080/hello
```

n'existera donc plus.

---

## Test généré

Supprimer le test :

```text
src/test/java/com/ganatan/starter/GreetingResourceTest.java
```

Un nouveau test sera créé ici :

```text
src/test/java/com/ganatan/starter/api/root/RootControllerTest.java
```

---

# Dépendance REST JSON

Le controller retourne des objets JSON.

Vérifier que l'extension suivante est présente dans :

```text
pom.xml
```

```xml
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest-jackson</artifactId>
</dependency>
```

Cette extension fournit :

```text
Quarkus REST
Jackson
conversion automatique Java → JSON
```

---

# Configuration Maven

Mettre à jour les métadonnées du projet dans :

```text
pom.xml
```

```xml
<groupId>com.ganatan.starter</groupId>
<artifactId>quarkus-starter</artifactId>
<version>1.0.0</version>
<name>quarkus-starter</name>
<description>Demo project for Quarkus</description>
```

---

# RootController

## Classe

```text
com.ganatan.starter.api.root.RootController
```

## Chemin

```text
src/main/java/com/ganatan/starter/api/root/RootController.java
```

## Code

```java
package com.ganatan.starter.api.root;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class RootController {

  public record ApiInfo(
      String application,
      String status,
      String java
  ) {
  }

  @GET
  public Map<String, Object> root() {

    return Map.of(
        "application",
        "quarkus-starter",
        "status",
        "running",
        "java",
        System.getProperty(
            "java.version"
        )
    );

  }

  @GET
  @Path("info")
  public ApiInfo rootWithRecord() {

    return new ApiInfo(
        "quarkus-starter",
        "running",
        System.getProperty(
            "java.version"
        )
    );

  }

  @GET
  @Path("status")
  public Map<String, Object> rootWithHashMap() {

    Map<String, Object> response =
        new HashMap<>();

    response.put(
        "application",
        "quarkus-starter"
    );

    response.put(
        "status",
        "running"
    );

    response.put(
        "java",
        System.getProperty(
            "java.version"
        )
    );

    return response;

  }

}
```

---

# Test

## Classe

```text
com.ganatan.starter.api.root.RootControllerTest
```

## Chemin

```text
src/test/java/com/ganatan/starter/api/root/RootControllerTest.java
```

## Code

```java
package com.ganatan.starter.api.root;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RootControllerTest {

  @Test
  void shouldReturnApplicationInformation() {

    given()
        .when()
        .get("/")
        .then()
        .statusCode(200)
        .body(
            "application",
            is("quarkus-starter")
        )
        .body(
            "status",
            is("running")
        );

  }

}
```

---

# Lancement en mode développement

Se placer dans le dossier contenant :

```text
pom.xml
```

---

## Avec Maven installé

```bash
mvn quarkus:dev
```

---

## Windows avec Maven Wrapper

```powershell
.\mvnw.cmd quarkus:dev
```

---

# Lancement avec IntelliJ IDEA

Ouvrir :

```text
Run
Edit Configurations
```

Cliquer sur :

```text
Add new run configuration...
```

Sélectionner :

```text
Maven
```

Configurer :

```text
Name              : Quarkus Dev
Command line      : quarkus:dev
Working directory : $ProjectFileDir$
Profiles          :
```

Le champ :

```text
Profiles
```

reste vide.

Cliquer sur :

```text
Apply
OK
```

Dans la barre supérieure d'IntelliJ, sélectionner :

```text
Quarkus Dev
```

Puis :

```text
Run ▶
```

Cette configuration exécute l'équivalent de :

```bash
mvn quarkus:dev
```

---

# Debug avec IntelliJ IDEA

Utiliser la même configuration :

```text
Quarkus Dev
```

Puis cliquer sur :

```text
Debug 🐞
```

Les breakpoints placés dans le code Java sont alors actifs.

---

# Tests

## Avec Maven

```bash
mvn test
```

---

## Windows avec Maven Wrapper

```powershell
.\mvnw.cmd test
```

---

# Build

## Avec Maven

```bash
mvn clean package
```

---

## Windows avec Maven Wrapper

```powershell
.\mvnw.cmd clean package
```

---

# Structure du build Quarkus

Le dossier contient notamment :

```text
target/quarkus-app/
├── app/
├── lib/
├── quarkus/
└── quarkus-run.jar
```

Contrairement à un JAR Spring Boot classique, le fichier :

```text
quarkus-run.jar
```

ne doit pas être utilisé seul.

Le dossier complet :

```text
target/quarkus-app/
```

doit être conservé.

---

# Exécution de la version compilée

Après :

```bash
mvn clean package
```

lancer :

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

L'application écoute sur :

```text
http://localhost:3000
```

Tester :

```bash
curl http://localhost:3000/
```

---

# Installation Maven locale

Compiler, tester et installer l'artefact dans le repository Maven local :

```bash
mvn clean install
```

Avec Maven Wrapper sous Windows :

```powershell
.\mvnw.cmd clean install
```

Avec Maven Wrapper sous Linux :

```bash
./mvnw clean install
```

---
# Commandes Maven usuelles

```text
mvn clean                                # Nettoie les fichiers générés
mvn compile                              # Compile le projet
mvn test                                 # Exécute les tests
mvn package                              # Compile, teste et génère le package
mvn verify                               # Vérifie le package et exécute les contrôles
mvn install                              # Installe le package dans le repository Maven local
mvn clean package                        # Nettoie puis génère le package
mvn clean install                        # Nettoie, teste, package et installe localement
mvn dependency:tree                      # Affiche l'arbre des dépendances
mvn versions:display-dependency-updates  # Affiche les mises à jour disponibles des dépendances
mvn versions:display-plugin-updates      # Affiche les mises à jour disponibles des plugins
mvn quarkus:dev                          # Lance Quarkus en mode développement
java -jar target/quarkus-app/quarkus-run.jar  # Lance la version compilée
```
---

