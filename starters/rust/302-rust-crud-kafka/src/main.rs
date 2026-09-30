use std::{collections::BTreeMap, env, sync::Arc, time::Duration};

use axum::{
    extract::State,
    routing::{get, post},
    Json, Router,
};
use chrono::Utc;
use dotenvy::dotenv;
use rskafka::{
    client::{
        partition::{Compression, PartitionClient, UnknownTopicHandling},
        ClientBuilder,
    },
    record::Record,
};
use serde::Deserialize;
use serde_json::{json, Value};
use tokio::sync::RwLock;

#[derive(Clone)]
struct Config {
    application: String,
    address: String,
    port: String,
    bootstrap: String,
    topic: String,
    group: String,
}

impl Config {
    fn from_env() -> Self {
        Self {
            application: var("APPLICATION_NAME", "rust-kafka"),
            address: var("SERVER_ADDRESS", "127.0.0.1"),
            port: var("SERVER_PORT", "3000"),
            bootstrap: var("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092"),
            topic: var("KAFKA_TOPIC", "media"),
            group: var("KAFKA_GROUP_ID", "ganatan-group"),
        }
    }
}

fn var(key: &str, default: &str) -> String {
    env::var(key).unwrap_or_else(|_| default.to_string())
}

#[derive(Clone)]
struct AppState {
    config: Arc<Config>,
    partition: Arc<PartitionClient>,
    messages: Arc<RwLock<Vec<String>>>,
}

#[derive(Deserialize)]
struct KafkaMessage {
    message: String,
}

async fn status(State(state): State<AppState>) -> Json<Value> {
    Json(json!({
        "application": state.config.application,
        "status": "running",
        "topic": state.config.topic
    }))
}

async fn send(State(state): State<AppState>, Json(body): Json<KafkaMessage>) -> Json<Value> {
    let record = Record {
        key: None,
        value: Some(body.message.as_bytes().to_vec()),
        headers: BTreeMap::new(),
        timestamp: Utc::now(),
    };

    state
        .partition
        .produce(vec![record], Compression::default())
        .await
        .unwrap();

    Json(json!({
        "status": "sent",
        "message": body.message
    }))
}

async fn list_messages(State(state): State<AppState>) -> Json<Vec<String>> {
    Json(state.messages.read().await.clone())
}

async fn consume(partition: Arc<PartitionClient>, messages: Arc<RwLock<Vec<String>>>) {
    let mut offset = 0;

    loop {
        match partition.fetch_records(offset, 1..1_000_000, 1_000).await {
            Ok((records, _)) => {
                for record in records {
                    offset = record.offset + 1;

                    if let Some(value) = record.record.value {
                        if let Ok(message) = String::from_utf8(value) {
                            messages.write().await.push(message);
                        }
                    }
                }
            }
            Err(error) => eprintln!("Kafka consumer error: {error}"),
        }

        tokio::time::sleep(Duration::from_millis(500)).await;
    }
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let config = Arc::new(Config::from_env());

    let client = ClientBuilder::new(vec![config.bootstrap.clone()])
        .build()
        .await
        .unwrap();

    let controller = client.controller_client().unwrap();
    let _ = controller.create_topic(&config.topic, 1, 1, 5_000).await;

    let partition = Arc::new(
        client
            .partition_client(config.topic.clone(), 0, UnknownTopicHandling::Retry)
            .await
            .unwrap(),
    );

    let messages = Arc::new(RwLock::new(Vec::<String>::new()));

    tokio::spawn(consume(partition.clone(), messages.clone()));

    let state = AppState {
        config: config.clone(),
        partition,
        messages,
    };

    let app = Router::new()
        .route("/kafka", get(status))
        .route("/kafka/send", post(send))
        .route("/kafka/messages", get(list_messages))
        .with_state(state);

    let listener = tokio::net::TcpListener::bind(format!("{}:{}", config.address, config.port))
        .await
        .unwrap();

    println!("{} (group {})", config.application, config.group);
    println!("http://localhost:{}/kafka", config.port);

    axum::serve(listener, app).await.unwrap();
}