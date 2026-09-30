use std::{env, sync::Arc, time::Duration};

use axum::{
    extract::State,
    routing::{get, post},
    Json, Router,
};
use dotenvy::dotenv;
use futures::StreamExt;
use rdkafka::{
    consumer::{Consumer, StreamConsumer},
    producer::{FutureProducer, FutureRecord},
    ClientConfig, Message,
};
use serde::{Deserialize, Serialize};
use tokio::sync::RwLock;

#[derive(Clone)]
struct AppState {
    producer: FutureProducer,
    messages: Arc<RwLock<Vec<String>>>,
    topic: String,
}

#[derive(Serialize)]
struct KafkaStatus {
    status: String,
    topic: String,
}

#[derive(Deserialize)]
struct KafkaMessage {
    message: String,
}

#[derive(Serialize)]
struct KafkaResponse {
    status: String,
    message: String,
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let address = env::var("SERVER_ADDRESS").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("SERVER_PORT").unwrap_or_else(|_| "3000".to_string());
    let bootstrap_servers = env::var("KAFKA_BOOTSTRAP_SERVERS").unwrap_or_else(|_| "localhost:9092".to_string());
    let topic = env::var("KAFKA_TOPIC").unwrap_or_else(|_| "media".to_string());
    let group_id = env::var("KAFKA_GROUP_ID").unwrap_or_else(|_| "ganatan-rust-group".to_string());

    let producer: FutureProducer = ClientConfig::new()
        .set("bootstrap.servers", &bootstrap_servers)
        .create()
        .expect("Kafka producer creation failed");

    let consumer: StreamConsumer = ClientConfig::new()
        .set("group.id", &group_id)
        .set("bootstrap.servers", &bootstrap_servers)
        .set("auto.offset.reset", "earliest")
        .create()
        .expect("Kafka consumer creation failed");

    consumer.subscribe(&[&topic]).expect("Kafka subscription failed");

    let messages = Arc::new(RwLock::new(Vec::new()));
    let consumer_messages = messages.clone();

    tokio::spawn(async move {
        let mut stream = consumer.stream();

        while let Some(result) = stream.next().await {
            if let Ok(message) = result {
                if let Some(Ok(payload)) = message.payload_view::<str>() {
                    consumer_messages.write().await.push(payload.to_string());
                }
            }
        }
    });

    let state = AppState {
        producer,
        messages,
        topic,
    };

    let app = Router::new()
        .route("/kafka", get(status))
        .route("/kafka/send", post(send))
        .route("/kafka/messages", get(messages))
        .with_state(state);

    let listener = tokio::net::TcpListener::bind(format!("{address}:{port}"))
        .await
        .unwrap();

    axum::serve(listener, app).await.unwrap();
}

async fn status(State(state): State<AppState>) -> Json<KafkaStatus> {
    Json(KafkaStatus {
        status: "running".to_string(),
        topic: state.topic,
    })
}

async fn send(
    State(state): State<AppState>,
    Json(body): Json<KafkaMessage>,
) -> Json<KafkaResponse> {
    let record = FutureRecord::<(), String>::to(&state.topic).payload(&body.message);

    state
        .producer
        .send(record, Duration::from_secs(5))
        .await
        .expect("Kafka send failed");

    Json(KafkaResponse {
        status: "sent".to_string(),
        message: body.message,
    })
}

async fn messages(State(state): State<AppState>) -> Json<Vec<String>> {
    Json(state.messages.read().await.clone())
}