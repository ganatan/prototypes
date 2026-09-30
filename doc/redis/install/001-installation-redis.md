# REDIS

# Redis + Redis Insight avec Docker Compose

Structure :

```text
REDIS-STARTER/
├─ docker/
│  └─ compose.yml
└─ scripts/
   ├─ redis-up.bat
   ├─ redis-down.bat
   ├─ redis-clean.bat
   ├─ redis-ps.bat
   ├─ redis-logs.bat
   └─ redis-cli.bat
```

------------------------------------------------------------------------

# Docker Compose

## docker/compose.yml

```yaml
services:
  redis:
    image: redis:7.4.0
    container_name: ganatan-redis
    ports:
      - "6379:6379"
    command: ["redis-server", "--appendonly", "yes"]
    volumes:
      - redis_data:/data

  redis-insight:
    image: redis/redisinsight:latest
    container_name: ganatan-redis-insight
    ports:
      - "5540:5540"
    depends_on:
      - redis

volumes:
  redis_data:
```

------------------------------------------------------------------------

# Scripts Windows

## scripts/redis-up.bat

```bat
@echo off
docker compose -f docker\compose.yml up -d
pause
```

## scripts/redis-down.bat

```bat
@echo off
docker compose -f docker\compose.yml down
pause
```

## scripts/redis-clean.bat

Reset complet avec suppression des volumes :

```bat
@echo off
docker compose -f docker\compose.yml down -v --remove-orphans
pause
```

## scripts/redis-ps.bat

```bat
@echo off
docker compose -f docker\compose.yml ps
pause
```

## scripts/redis-logs.bat

```bat
@echo off
docker compose -f docker\compose.yml logs -f --tail=200
pause
```

## scripts/redis-cli.bat

```bat
@echo off
docker exec -it ganatan-redis redis-cli
```

------------------------------------------------------------------------

# Démarrage

```bash
scripts\redis-up.bat
```

Vérifier les conteneurs :

```bash
scripts\redis-ps.bat
```

Résultat attendu :

```text
ganatan-redis
ganatan-redis-insight
```

------------------------------------------------------------------------

# Vérifier Redis

Entrer dans Redis CLI :

```bash
scripts\redis-cli.bat
```

Tester Redis :

```text
PING
```

Résultat :

```text
PONG
```

Tester l'écriture :

```text
SET movie:1 Interstellar
```

Résultat :

```text
OK
```

Tester la lecture :

```text
GET movie:1
```

Résultat :

```text
"Interstellar"
```

Supprimer la clé :

```text
DEL movie:1
```

Quitter :

```text
QUIT
```

------------------------------------------------------------------------

# Redis Insight

Redis Insight fournit une interface graphique web permettant de visualiser et administrer Redis.

Accès :

```text
http://localhost:5540
```

Lors de la première connexion à Redis :

```text
Host : redis
Port : 6379
```

Le host est `redis` car Redis Insight et Redis fonctionnent dans le même réseau Docker.

Depuis une application exécutée directement sur Windows :

```text
Host : localhost
Port : 6379
```

------------------------------------------------------------------------

# Architecture

```text
Windows
   |
   +--> localhost:6379
   |        |
   |        +--> Redis
   |
   +--> http://localhost:5540
            |
            +--> Redis Insight
                     |
                     +--> redis:6379
```

------------------------------------------------------------------------

# Utilisation avec Spring Boot

Une application Spring Boot exécutée localement utilisera :

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

Architecture :

```text
Postman
   |
   | HTTP
   v
Spring Boot
   |
   | Redis TCP
   v
Redis
```

------------------------------------------------------------------------

# Utilisation avec Postman

Postman ne communique pas directement avec Redis.

Redis utilise son propre protocole TCP sur :

```text
localhost:6379
```

Postman utilise HTTP.

Il faut donc passer par une API HTTP, par exemple :

```text
POST http://localhost:3000/redis
GET http://localhost:3000/redis/movie:1
DELETE http://localhost:3000/redis/movie:1
```

Exemple :

```text
POST http://localhost:3000/redis
```

Body :

```json
{
  "key": "movie:1",
  "value": "Interstellar"
}
```

Spring Boot effectue ensuite :

```text
SET movie:1 Interstellar
```

Lecture :

```text
GET http://localhost:3000/redis/movie:1
```

Spring Boot effectue :

```text
GET movie:1
```

Suppression :

```text
DELETE http://localhost:3000/redis/movie:1
```

Spring Boot effectue :

```text
DEL movie:1
```

------------------------------------------------------------------------

# Vérification avec Redis Insight

Après un appel Postman :

```text
POST http://localhost:3000/redis
```

avec :

```json
{
  "key": "movie:1",
  "value": "Interstellar"
}
```

ouvrir :

```text
http://localhost:5540
```

La clé suivante doit apparaître :

```text
movie:1
```

avec la valeur :

```text
Interstellar
```

------------------------------------------------------------------------

# Workflow

Démarrer Redis et Redis Insight :

```bash
scripts\redis-up.bat
```

Vérifier :

```bash
scripts\redis-ps.bat
```

Tester Redis :

```bash
scripts\redis-cli.bat
```

Interface graphique :

```text
http://localhost:5540
```

Tester l'API avec Postman :

```text
Postman
→ Spring Boot :3000
→ Redis :6379
```

Arrêter :

```bash
scripts\redis-down.bat
```

Reset complet :

```bash
scripts\redis-clean.bat
```

------------------------------------------------------------------------

# Accès

Redis :

```text
Host : localhost
Port : 6379
Protocole : Redis TCP
```

Redis Insight :

```text
http://localhost:5540
```

Connexion Redis depuis Redis Insight :

```text
Host : redis
Port : 6379
```

API Spring Boot :

```text
http://localhost:3000
```

Postman :

```text
Postman
→ HTTP
→ Spring Boot
→ Redis
```