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
    application: String,
    status: String,
    topic: String,
}

#[derive(Deserialize)]
struct KafkaMessage {
    message: String,
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let application = env::var("APPLICATION_NAME").unwrap_or_else(|_| "rust-kafka".to_string());
    let address = env::var("SERVER_ADDRESS").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("SERVER_PORT").unwrap_or_else(|_| "3000".to_string());
    let bootstrap = env::var("KAFKA_BOOTSTRAP_SERVERS").unwrap_or_else(|_| "localhost:9092".to_string());
    let topic = env::var("KAFKA_TOPIC").unwrap_or_else(|_| "media".to_string());
    let group = env::var("KAFKA_GROUP_ID").unwrap_or_else(|_| "rust-kafka-group".to_string());

    let producer = ClientConfig::new()
        .set("bootstrap.servers", &bootstrap)
        .create()
        .unwrap();

    let consumer: StreamConsumer = ClientConfig::new()
        .set("bootstrap.servers", &bootstrap)
        .set("group.id", &group)
        .set("auto.offset.reset", "earliest")
        .create()
        .unwrap();

    consumer.subscribe(&[&topic]).unwrap();

    let messages = Arc::new(RwLock::new(Vec::new()));
    let received = messages.clone();

    tokio::spawn(async move {
        let mut stream = consumer.stream();

        while let Some(Ok(message)) = stream.next().await {
            if let Some(Ok(payload)) = message.payload_view::<str>() {
                received.write().await.push(payload.to_string());
            }
        }
    });

    let state = AppState {
        producer,
        messages,
        topic: topic.clone(),
    };

    let app = Router::new()
        .route("/", get({
            let application = application.clone();
            let topic = topic.clone();
            move || root(application.clone(), topic.clone())
        }))
        .route("/kafka/send", post(send))
        .route("/kafka/messages", get(messages))
        .with_state(state);

    let listener = tokio::net::TcpListener::bind(format!("{address}:{port}")).await.unwrap();

    axum::serve(listener, app).await.unwrap();
}

async fn root(application: String, topic: String) -> Json<KafkaStatus> {
    Json(KafkaStatus {
        application,
        status: "running".to_string(),
        topic,
    })
}

async fn send(State(state): State<AppState>, Json(body): Json<KafkaMessage>) -> Json<KafkaMessage> {
    state.producer
        .send(
            FutureRecord::<(), String>::to(&state.topic).payload(&body.message),
            Duration::from_secs(5),
        )
        .await
        .unwrap();

    Json(body)
}

async fn messages(State(state): State<AppState>) -> Json<Vec<String>> {
    Json(state.messages.read().await.clone())
}