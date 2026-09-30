# REDIS

# Version 1 — Redis seul

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

volumes:
  redis_data:
```

------------------------------------------------------------------------

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
docker compose -f docker\compose.yml logs -f --tail=200 redis
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

Vérifier :

```bash
scripts\redis-ps.bat
```

------------------------------------------------------------------------

# Vérifier Redis

```bash
scripts\redis-cli.bat
```

Puis :

```text
PING
```

Résultat :

```text
PONG
```

Test écriture :

```text
SET movie:1 Interstellar
```

Lecture :

```text
GET movie:1
```

Résultat :

```text
"Interstellar"
```

Suppression :

```text
DEL movie:1
```

Quitter :

```text
QUIT
```

------------------------------------------------------------------------

# Accès

```text
Host : localhost
Port : 6379
Protocole : Redis TCP
```

Pas d'interface web.

Pas d'URL HTTP.


------------------------------------------------------------------------

# Version 2 — Redis + Redis Insight

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

Vérifier :

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

```bash
scripts\redis-cli.bat
```

Puis :

```text
PING
SET movie:1 Interstellar
GET movie:1
DEL movie:1
QUIT
```

------------------------------------------------------------------------

# Redis Insight

Interface web :

```text
http://localhost:5540
```

Connexion à Redis depuis Redis Insight :

```text
Host : redis
Port : 6379
```

Redis Insight utilise `redis` comme host car les deux services sont dans le même réseau Docker.

------------------------------------------------------------------------

# Accès depuis Windows

Redis :

```text
Host : localhost
Port : 6379
```

Redis Insight :

```text
http://localhost:5540
```

------------------------------------------------------------------------

# Utilisation avec Spring Boot

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
Spring Boot :3000
   |
   | Redis TCP
   v
Redis :6379

Navigateur
   |
   | HTTP
   v
Redis Insight :5540
   |
   v
Redis :6379
```

------------------------------------------------------------------------

# Utilisation avec Postman

Postman ne communique pas directement avec Redis.

Il communique avec ton API Spring Boot :

```text
POST http://localhost:3000/redis
GET http://localhost:3000/redis/movie:1
DELETE http://localhost:3000/redis/movie:1
```

Spring Boot communique ensuite avec Redis sur :

```text
localhost:6379
```

------------------------------------------------------------------------

