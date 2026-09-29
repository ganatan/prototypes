use dotenvy::dotenv;
use std::env;
use tokio::net::UdpSocket;

#[tokio::main]
async fn main() {
    dotenv().ok();

    let address = env::var("UDP_ADDRESS").unwrap_or_else(|_| "127.0.0.1:5000".to_string());

    let socket = UdpSocket::bind(&address).await.unwrap();
    let mut buffer = [0u8; 2048];

    loop {
        let (size, source) = socket.recv_from(&mut buffer).await.unwrap();
        let message = String::from_utf8_lossy(&buffer[..size]);

        println!("UDP received from {} on {} -> {}", source, address, message);
    }
}