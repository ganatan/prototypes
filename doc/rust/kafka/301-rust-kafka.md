**## Projet rust-kafka**


**## Création du projet**

```bash
cargo new rust-kafka
cd rust-kafka
```

**## Dépendances**

Ajouter dans `Cargo.toml` :

```toml
[package]
name = "rust-kafka"
version = "0.1.0"
edition = "2024"

[dependencies]
axum = "0.8"
dotenvy = "0.15"
futures = "0.3"
rdkafka = { version = "0.38", features = ["cmake-build"] }
serde = { version = "1", features = ["derive"] }
serde_json = "1"
tokio = { version = "1", features = ["full"] }
```

**## Configuration locale**

Créer `.env` à la racine :

```env
SERVER_ADDRESS=127.0.0.1
SERVER_PORT=3000
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_TOPIC=media
KAFKA_GROUP_ID=ganatan-rust-group
```

**## src/main.rs**

```rust
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

Démarrer Kafka :

```bash
docker compose up -d
```

Compiler :

```bash
cargo check
```

Lancer l'application :

```bash
cargo run
```

**## Vérifier l'API**

```text
GET http://localhost:3000/kafka
```

Résultat :

```json
{
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
  "status": "sent",
  "message": "Interstellar"
}
```

Avec curl :

```bash
curl -X POST http://localhost:3000/kafka/send -H "Content-Type: application/json" -d "{\"message\":\"Interstellar\"}"
```

Envoyer d'autres messages :

```json
{
  "message": "Dune"
}
```

```json
{
  "message": "Alien"
}
```

**## Lire les messages**

```text
GET http://localhost:3000/kafka/messages
```

Résultat :

```json
[
  "Interstellar",
  "Dune",
  "Alien"
]
```

**## Kafka UI**

```text
http://localhost:8085
```

Le topic utilisé est :

```text
media
```

Le consumer group Rust est :

```text
ganatan-rust-group
```