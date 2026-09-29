use serde::{Deserialize, Serialize};
use std::fs;
use tokio::net::UdpSocket;
use tokio::time::{sleep, Duration};

#[derive(Deserialize)]
struct Config {
    address: String,
    #[serde(rename = "intervalMs")]
    interval_ms: u64,
}

#[derive(Serialize)]
struct Signal {
    distance: u32,
    name: String,
}

#[tokio::main]
async fn main() {
    let config: Config = serde_json::from_str(&fs::read_to_string("config.json").unwrap()).unwrap();
    let socket = UdpSocket::bind("127.0.0.1:0").await.unwrap();
    let mut index = 1;

    loop {
        let signal = Signal {
            distance: 1000 + index * 100,
            name: format!("target-{:03}", index),
        };

        let message = serde_json::to_string(&signal).unwrap();


        socket.send_to(message.as_bytes(), &config.address).await.unwrap();
        println!("UDP sent to {} -> {}", config.address, message);

        index += 1;
        sleep(Duration::from_millis(config.interval_ms)).await;
    }
}