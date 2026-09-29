use serde::Deserialize;
use std::fs;
use tokio::net::UdpSocket;

#[derive(Deserialize)]
struct Config {
    address: String,
}

#[tokio::main]
async fn main() {
    let config: Config = serde_json::from_str(&fs::read_to_string("config-receiver.json").unwrap()).unwrap();
    let socket = UdpSocket::bind(&config.address).await.unwrap();
    let mut buffer = [0u8; 2048];

    loop {
        let (size, source) = socket.recv_from(&mut buffer).await.unwrap();
        let message = String::from_utf8_lossy(&buffer[..size]);

        println!("UDP received from {} on {} -> {}", source, config.address, message);
    }
}