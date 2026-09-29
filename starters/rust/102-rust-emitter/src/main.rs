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

    let address = env::var("UDP_ADDRESS").expect("UDP_ADDRESS is required");
    let interval_ms = env::var("UDP_INTERVAL_MS").expect("UDP_INTERVAL_MS is required").parse::<u64>().expect("UDP_INTERVAL_MS must be a number");

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

// use serde::{Deserialize, Serialize};
// use std::fs;
// use tokio::net::UdpSocket;
// use tokio::time::{sleep, Duration};

// #[derive(Deserialize)]
// struct Config {
//     address: String,
//     #[serde(rename = "intervalMs")]
//     interval_ms: u64,
// }

// #[derive(Serialize)]
// struct Signal {
//     distance: u32,
//     name: String,
// }

// #[tokio::main]
// async fn main() {
//     let config: Config = serde_json::from_str(&fs::read_to_string("config-emitter.json").unwrap()).unwrap();
//     let socket = UdpSocket::bind("127.0.0.1:0").await.unwrap();
//     let mut index = 1;

//     loop {
//         let signal = Signal {
//             distance: 1000 + index * 100,
//             name: format!("target-{:03}", index),
//         };

//         let message = serde_json::to_string(&signal).unwrap();


//         socket.send_to(message.as_bytes(), &config.address).await.unwrap();
//         println!("UDP sent to {} -> {}", config.address, message);

//         index += 1;
//         sleep(Duration::from_millis(config.interval_ms)).await;
//     }
// }