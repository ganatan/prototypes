use dotenvy::dotenv;
use serde::Serialize;
use std::env;
use tokio::net::UdpSocket;
use tokio::time::{sleep, Duration};

#[derive(Serialize)]
struct Signal {
    distance: u32,
    name: String,
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let address = env::var("UDP_ADDRESS").unwrap_or_else(|_| "127.0.0.1:5000".to_string());
    let interval_ms = env::var("UDP_INTERVAL_MS").unwrap_or_else(|_| "1000".to_string()).parse::<u64>().unwrap();

    let socket = UdpSocket::bind("127.0.0.1:0").await.unwrap();
    let mut index = 1;

    loop {
        let signal = Signal {
            distance: 1000 + index * 100,
            name: format!("target-{:03}", index),
        };

        let message = serde_json::to_string(&signal).unwrap();

        socket.send_to(message.as_bytes(), &address).await.unwrap();
        println!("UDP sent to {} -> {}", address, message);

        index += 1;
        sleep(Duration::from_millis(interval_ms)).await;
    }
}