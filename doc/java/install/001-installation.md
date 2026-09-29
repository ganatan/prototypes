# Installation Java 8 avec IntelliJ

## 1. Installer Java 8

Télécharger un JDK 8 :

Oracle :

https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html

ou Adoptium :

https://adoptium.net/temurin/releases/?version=8

Installer par exemple dans :

```text
D:\hal\java\jdk-08
```

## 2. Configurer JAVA_HOME

Créer la variable :

```text
JAVA_HOME
```

Valeur :

```text
D:\hal\java\jdk-08
```

Ajouter dans `PATH` :

```text
%JAVA_HOME%\bin
```

## 3. Vérifier Java

```bash
java -version
javac -version
```

Résultat attendu :

```text
java version "1.8.0_xxx"
javac 1.8.0_xxx
```

## 4. Installer IntelliJ Community

Télécharger :

https://www.jetbrains.com/idea/download/

Installer IntelliJ IDEA Community Edition.

## 5. Créer le projet

Dans IntelliJ :

```text
New Project
```

Choisir :

```text
Java
```

Sélectionner le JDK :

```text
D:\hal\java\jdk-08
```

Nom :

```text
java8-training
```

## 6. Vérifier Java 8

Dans IntelliJ :

```text
Project SDK : 1.8
Project language level : 8
Target bytecode version : 8
```

## 7. Premier programme

Créer :

```text
src\HelloWorld.java
```

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Bonjour Danny");
    }
}
```

## 8. Exécuter

Dans IntelliJ :

```text
Run
```

ou :

```text
Shift + F10
```

Résultat :

```text
Bonjour Danny
```

## Environnement

```text
Java 8
IntelliJ Community
Maven
```