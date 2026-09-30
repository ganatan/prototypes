# Projet rust-crud-redis

## Projet

```bash
cargo new rust-redis
cd rust-redis
```

## Configuration Rust

Créer un fichier `.env` à la racine :

```env
APPLICATION_NAME=rust-redis
SERVER_ADDRESS=127.0.0.1
SERVER_PORT=3000
REDIS_HOST=127.0.0.1
REDIS_PORT=6379
```

## Configuration Cargo

`Cargo.toml` :

```toml
[package]
name = "rust-redis"
version = "0.1.0"
edition = "2021"

[dependencies]
axum = "0.8"
tokio = { version = "1", features = ["full"] }
redis = { version = "1", features = ["tokio-comp"] }
serde = { version = "1", features = ["derive"] }
serde_json = "1"
dotenvy = "0.15"
```

## src/main.rs

```rust
use std::{env, sync::Arc};

use axum::{
    extract::{Path, State},
    http::StatusCode,
    routing::{delete, get, post},
    Json, Router,
};
use dotenvy::dotenv;
use redis::{aio::MultiplexedConnection, AsyncCommands};
use serde::Deserialize;
use serde_json::{json, Value};

#[derive(Clone)]
struct Config {
    application: String,
    address: String,
    port: String,
    redis_host: String,
    redis_port: String,
}

impl Config {
    fn from_env() -> Self {
        Self {
            application: var("APPLICATION_NAME", "rust-redis"),
            address: var("SERVER_ADDRESS", "127.0.0.1"),
            port: var("SERVER_PORT", "3000"),
            redis_host: var("REDIS_HOST", "127.0.0.1"),
            redis_port: var("REDIS_PORT", "6379"),
        }
    }
}

fn var(key: &str, default: &str) -> String {
    env::var(key).unwrap_or_else(|_| default.to_string())
}

#[derive(Clone)]
struct AppState {
    config: Arc<Config>,
    redis: MultiplexedConnection,
}

#[derive(Deserialize)]
struct RedisMessage {
    key: String,
    value: String,
}

async fn status(State(state): State<AppState>) -> Json<Value> {
    Json(json!({
        "application": state.config.application,
        "status": "running",
        "database": "redis"
    }))
}

async fn set_value(State(state): State<AppState>, Json(body): Json<RedisMessage>) -> Json<Value> {
    let mut redis = state.redis.clone();
    let _: () = redis.set(&body.key, &body.value).await.unwrap();

    Json(json!({
        "status": "saved",
        "key": body.key,
        "value": body.value
    }))
}

async fn get_value(
    State(state): State<AppState>,
    Path(key): Path<String>,
) -> Result<Json<Value>, StatusCode> {
    let mut redis = state.redis.clone();
    let value: Option<String> = redis.get(&key).await.unwrap();

    match value {
        Some(value) => Ok(Json(json!({
            "key": key,
            "value": value
        }))),
        None => Err(StatusCode::NOT_FOUND),
    }
}

async fn delete_value(State(state): State<AppState>, Path(key): Path<String>) -> Json<Value> {
    let mut redis = state.redis.clone();
    let deleted: usize = redis.del(&key).await.unwrap();

    Json(json!({
        "status": "deleted",
        "key": key,
        "deleted": deleted > 0
    }))
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let config = Arc::new(Config::from_env());
    let redis_url = format!("redis://{}:{}/", config.redis_host, config.redis_port);

    let client = redis::Client::open(redis_url).unwrap();
    let redis = client.get_multiplexed_async_connection().await.unwrap();

    let state = AppState {
        config: config.clone(),
        redis,
    };

    let app = Router::new()
        .route("/redis", get(status).post(set_value))
        .route("/redis/{key}", get(get_value).delete(delete_value))
        .with_state(state);

    let listener = tokio::net::TcpListener::bind(format!("{}:{}", config.address, config.port))
        .await
        .unwrap();

    println!("{}", config.application);
    println!("Redis {}:{}", config.redis_host, config.redis_port);
    println!("http://localhost:{}/redis", config.port);

    axum::serve(listener, app).await.unwrap();
}
```

## Docker Compose

Créer `docker-compose.yml` :

```yaml
services:
  redis:
    image: redis:7.4.0
    container_name: redis-starter
    ports:
      - "6379:6379"
    command: ["redis-server", "--appendonly", "yes"]
    volumes:
      - redis_data:/data

  redis-insight:
    image: redis/redisinsight:latest
    container_name: redis-starter-ui
    ports:
      - "5540:5540"
    depends_on:
      - redis

volumes:
  redis_data:
```

## Exécution

```bash
docker compose up -d
cargo check
cargo run
```

## Vérifier Redis

```bash
docker compose ps
```

Tester directement Redis :

```bash
docker exec -it redis-starter redis-cli
```

Puis :

```text
PING
```

Résultat :

```text
PONG
```

## Vérifier l'API

```text
GET http://localhost:3000/redis
```

Résultat :

```json
{
  "application": "rust-redis",
  "status": "running",
  "database": "redis"
}
```

## Enregistrer une valeur

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

Réponse :

```json
{
  "status": "saved",
  "key": "movie:1",
  "value": "Interstellar"
}
```

Cela correspond à la commande Redis :

```text
SET movie:1 Interstellar
```

## Lire une valeur

```text
GET http://localhost:3000/redis/movie:1
```

Résultat :

```json
{
  "key": "movie:1",
  "value": "Interstellar"
}
```

Cela correspond à :

```text
GET movie:1
```

## Supprimer une valeur

```text
DELETE http://localhost:3000/redis/movie:1
```

Résultat :

```json
{
  "status": "deleted",
  "key": "movie:1",
  "deleted": true
}
```

Cela correspond à :

```text
DEL movie:1
```

## Vérifier directement avec redis-cli

```bash
docker exec -it redis-starter redis-cli
```

Créer :

```text
SET movie:1 Interstellar
```

Lire :

```text
GET movie:1
```

Résultat :

```text
"Interstellar"
```

Supprimer :

```text
DEL movie:1
```

Quitter :

```text
QUIT
```

## Redis Insight

```text
http://localhost:5540
```

Pour connecter Redis Insight à Redis :

```text
Host : redis
Port : 6379
```

Redis Insight permet de visualiser directement les clés et les valeurs présentes dans Redis.

Après :

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

Redis Insight permet de visualiser :

```text
movie:1
→ Interstellar
```

## Principe

```text
POST /redis
    ↓
Axum
    ↓
redis-rs
    ↓
SET
    ↓
Redis
```

```text
GET /redis/movie:1
    ↓
Axum
    ↓
redis-rs
    ↓
GET
    ↓
Interstellar
```

```text
DELETE /redis/movie:1
    ↓
Axum
    ↓
redis-rs
    ↓
DEL
    ↓
Redis
```

## Architecture

```text
Postman
   |
   | HTTP
   v
Rust / Axum :3000
   |
   | Redis TCP
   v
Redis :6379
```

Avec Redis Insight :

```text
Navigateur
   |
   | HTTP
   v
Redis Insight :5540
   |
   v
Redis :6379
```

## Commandes Redis utilisées

```text
SET
GET
DEL
```

## Build

```bash
cargo build
cargo build --release
```

Artefacts générés :

```text
target/debug/rust-redis.exe
target/debug/rust-redis
target/release/rust-redis.exe
target/release/rust-redis
```