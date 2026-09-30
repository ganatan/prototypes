**## Configuration Rust**

Créer un fichier `.env` à la racine :

```env
APPLICATION_NAME=rust-kafka
SERVER_ADDRESS=127.0.0.1
SERVER_PORT=3000
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_TOPIC=media
KAFKA_GROUP_ID=ganatan-group
```

**## Projet**

```bash
cargo new rust-kafka
cd rust-kafka
```

**## Configuration Cargo**

`Cargo.toml` :

```toml
[package]
name = "rust-kafka"
version = "1.0.0"
edition = "2024"

[dependencies]
axum = "0.8"
dotenvy = "0.15"
kafka_client = "0.8"
serde = { version = "1", features = ["derive"] }
tokio = { version = "1", features = ["full"] }
```

**## src/main.rs**

```rust
use std::{env, sync::Arc};

use axum::{
    extract::State,
    routing::{get, post},
    Json, Router,
};
use dotenvy::dotenv;
use kafka_client::{Client, ConsumerConfig, Producer, ProducerRecord};
use serde::{Deserialize, Serialize};
use tokio::sync::RwLock;

#[derive(Clone)]
struct AppState {
    producer: Producer,
    topic: String,
    messages: Arc<RwLock<Vec<String>>>,
}

#[derive(Serialize)]
struct ApiInfo {
    application: String,
    status: String,
    topic: String,
}

#[derive(Deserialize, Serialize)]
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
    let group = env::var("KAFKA_GROUP_ID").unwrap_or_else(|_| "ganatan-group".to_string());

    let client = Client::builder(vec![bootstrap]).with_plaintext().build().await.unwrap();
    let producer = client.producer_default().await;

    let messages = Arc::new(RwLock::new(Vec::new()));
    let consumer_messages = messages.clone();
    let consumer_topic = topic.clone();

    let mut consumer = client.consumer(ConsumerConfig::new(group).with_earliest());
    consumer.subscribe(vec![consumer_topic]).await.unwrap();

    tokio::spawn(async move {
        loop {
            if let Ok(records) = consumer.poll().await {
                for record in records {
                    if let Ok(message) = String::from_utf8(record.value.to_vec()) {
                        consumer_messages.write().await.push(message);
                    }
                }
            }
        }
    });

    let state = AppState {
        producer,
        topic: topic.clone(),
        messages,
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

async fn root(application: String, topic: String) -> Json<ApiInfo> {
    Json(ApiInfo {
        application,
        status: "running".to_string(),
        topic,
    })
}

async fn send(State(state): State<AppState>, Json(body): Json<KafkaMessage>) -> Json<KafkaMessage> {
    state.producer
        .send(ProducerRecord::new(&state.topic, body.message.clone().into()))
        .await
        .unwrap();

    Json(body)
}

async fn messages(State(state): State<AppState>) -> Json<Vec<String>> {
    Json(state.messages.read().await.clone())
}
```

**## Docker Compose**

Créer `docker-compose.yml` :

```yaml
services:
  kafka:
    image: confluentinc/cp-kafka:7.6.1
    container_name: kafka-starter
    ports:
      - "9092:9092"
      - "29092:29092"
    environment:
      CLUSTER_ID: MkU3OEVBNTcwNTJENDM2Qk
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka:9093
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://0.0.0.0:9092,CONTROLLER://kafka:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT,CONTROLLER:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1

  kafka-ui:
    image: provectuslabs/kafka-ui:latest
    container_name: kafka-starter-ui
    ports:
      - "8085:8080"
    environment:
      KAFKA_CLUSTERS_0_NAME: local
      KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS: kafka:29092
    depends_on:
      - kafka
```

**## Exécution**

```bash
docker compose up -d
cargo check
cargo run
```

**## Vérifier l'API**

```text
GET http://localhost:3000/
```

Résultat :

```json
{
  "application": "rust-kafka",
  "status": "running",
  "topic": "media"
}
```

**## Envoyer un message**

```text
POST http://localhost:3000/kafka/send
```

Body :

```json
{
  "message": "Interstellar"
}
```

Réponse :

```json
{
  "message": "Interstellar"
}
```

**## Lire les messages**

```text
GET http://localhost:3000/kafka/messages
```

Résultat :

```json
[
  "Interstellar"
]
```

**## Kafka UI**

```text
http://localhost:8085
```

**## Build**

```bash
cargo build
cargo build --release
```

