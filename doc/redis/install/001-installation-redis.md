# REDIS

# Scripts Redis (Docker Compose) + scripts Windows

Structure :

```
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

```yml
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

Reset total (volumes + orphelins) :

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

Entrer dans le CLI Redis :

```bat
@echo off
docker exec -it ganatan-redis redis-cli
```

------------------------------------------------------------------------

# Workflow

Démarrer :

```bash
scripts\redis-up.bat
scripts\redis-ps.bat
```

CLI :

```bash
scripts\redis-cli.bat
```

Test rapide :

```text
PING
SET hello world
GET hello
DEL hello
QUIT
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
- host : `localhost`
- port : `6379`
