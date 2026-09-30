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