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

    // création du topic (erreur ignorée s'il existe déjà)
    let controller = client.controller_client().unwrap();
    let _ = controller.create_topic(&topic, 1, 1, 5_000).await;

    let partition = client
        .partition_client(topic.clone(), 0, UnknownTopicHandling::Retry)
        .await
        .unwrap();

    // produce
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

    // consume
    let (records, high_watermark) = partition
        .fetch_records(offsets[0], 1..1_000_000, 1_000)
        .await
        .unwrap();

    for r in records {
        let value = String::from_utf8(r.record.value.unwrap_or_default()).unwrap();
        println!("reçu offset {} : {}", r.offset, value);
    }
    println!("high watermark = {high_watermark}");
}