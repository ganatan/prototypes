## Projet

```bash
cargo new rust-kafka
cd rust-kafka
```

## Configuration Rust

Créer un fichier `.env` à la racine :

```env
APPLICATION_NAME=rust-kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
KAFKA_TOPIC=media
KAFKA_GROUP_ID=ganatan-group
```

## Configuration Cargo

`Cargo.toml` :

```toml
[package]
name = "rust-kafka"
version = "0.1.0"
edition = "2021"

[dependencies]
rskafka = "0.6"
tokio = { version = "1", features = ["full"] }
chrono = "0.4"
dotenvy = "0.15"
```

## src/main.rs

```rust
use std::{collections::BTreeMap, env};

use chrono::Utc;
use dotenvy::dotenv;
use rskafka::{
    client::{
        partition::{Compression, UnknownTopicHandling},
        ClientBuilder,
    },
    record::Record,
};

fn var(key: &str, default: &str) -> String {
    env::var(key).unwrap_or_else(|_| default.to_string())
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let application = var("APPLICATION_NAME", "rust-kafka");
    let bootstrap = var("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    let topic = var("KAFKA_TOPIC", "media");
    let group = var("KAFKA_GROUP_ID", "ganatan-group");

    println!("{application} (topic {topic}, group {group})");

    let client = ClientBuilder::new(vec![bootstrap]).build().await.unwrap();

    let controller = client.controller_client().unwrap();
    let _ = controller.create_topic(&topic, 1, 1, 5_000).await;

    let partition = client
        .partition_client(topic.clone(), 0, UnknownTopicHandling::Retry)
        .await
        .unwrap();

    let record = Record {
        key: None,
        value: Some(b"hello kafka".to_vec()),
        headers: BTreeMap::new(),
        timestamp: Utc::now(),
    };

    let offsets = partition
        .produce(vec![record], Compression::default())
        .await
        .unwrap();

    println!("envoyé, offset = {}", offsets[0]);

    let (records, high_watermark) = partition
        .fetch_records(offsets[0], 1..1_000_000, 1_000)
        .await
        .unwrap();

    for record in records {
        let value = String::from_utf8(record.record.value.unwrap_or_default()).unwrap();
        println!("reçu offset {} : {}", record.offset, value);
    }

    println!("high watermark = {high_watermark}");
}
```

## Docker Compose

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

## Exécution

Démarrer Kafka :

```bash
docker compose up -d
```

Vérifier le projet Rust :

```bash
cargo check
```

Exécuter :

```bash
cargo run
```


## Build

Compiler le projet :

```bash
cargo build
```

Compiler en mode release :

```bash
cargo build --release
```

