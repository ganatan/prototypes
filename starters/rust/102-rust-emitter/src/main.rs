use serde::Serialize;
use tokio::time::{sleep, Duration};

#[derive(Serialize)]
struct Signal {
    distance: u32,
    name: String,
}

#[tokio::main]
async fn main() {
    let mut index = 1;

    loop {
        let signal = Signal {
            distance: 1000 + index * 100,
            name: format!("target-{:03}", index),
        };

        println!("{}", serde_json::to_string(&signal).unwrap());

        index += 1;
        sleep(Duration::from_secs(1)).await;
    }
}