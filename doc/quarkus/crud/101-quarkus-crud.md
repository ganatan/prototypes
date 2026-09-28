# Projet Quarkus CRUD

[https://code.quarkus.io](https://code.quarkus.io)

```text
Group      : com.ganatan.starter   # Package Java
Artifact   : quarkus-starter       # Nom du projet
Build Tool : Maven                 # Gestion du build
Extensions : Aucune                # Configuration minimale
```

```text
Generate your application
```

## Lancement

```powershell
.\mvnw.cmd quarkus:dev
```


### Maven installé

```bash
mvn quarkus:dev
```

Tester :

```text
http://localhost:8080/hello
```

---

## IntelliJ IDEA

Ouvrir :

```text
Run
Edit Configurations
```

Créer une configuration :

```text
Type              : Maven
Name              : Quarkus starter
Run               : quarkus:dev
Working directory : $ProjectFileDir$
```

La valeur du champ `Run` doit être exactement :

```text
quarkus:dev
```

Ne pas utiliser :

```text
Quarkus Dev
```

sinon Maven retourne :

```text
Unknown lifecycle phase "Quarkus"
```

Lancer ensuite :

```text
Quarkus starter → Run ▶
```

---

## Debug IntelliJ

Créer une deuxième configuration :

```text
Type                 : Remote JVM Debug
Name                 : Quarkus debug
Debugger mode        : Attach to remote JVM
Transport            : Socket
Host                 : localhost
Port                 : 5005
Use module classpath : quarkus-starter
```

Ordre de lancement :

```text
1. Quarkus starter → Run ▶
2. Quarkus debug   → Debug 🐞
```

Quarkus utilise alors :

```text
HTTP  : localhost:8080
Debug : localhost:5005
```

Si IntelliJ affiche :

```text
Unable to open debugger port (localhost:5005)
Connection refused
```

cela signifie que Quarkus n'a pas encore été lancé.

---

## Compilation

### Windows

```powershell
.\mvnw.cmd clean package
```

ou :

```powershell
mvn clean package
```

## Exécution de la version compilée

```bash
java -jar target/quarkus-app/quarkus-run.jar
```

Conserver tout le dossier :

```text
target/quarkus-app/
```

Tester :

```text
http://localhost:8080/hello
```

---

