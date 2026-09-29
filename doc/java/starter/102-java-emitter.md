**## Projet rust-emitter**

**## Commandes essentielles**

```bash
cargo check                  # Vérifier le code sans générer l'exécutable
cargo run                    # Compiler puis exécuter le projet
cargo build                  # Compiler en mode debug
cargo build --release        # Compiler en mode optimisé
```

**## Exécutables générés**

```text
target/debug/rust-starter.exe      # Windows - build debug
target/debug/rust-starter          # Linux - build debug
target/release/rust-starter.exe    # Windows - build release
target/release/rust-starter        # Linux - build release
```

**## Dépendances**

```toml
[dependencies]
serde = { version = "1", features = ["derive"] }
serde_json = "1"
tokio = { version = "1", features = ["full"] }
```

**## Configuration**

Créer un fichier `config.json` à la racine :

```json
{
  "address": "127.0.0.1:5000",
  "intervalMs": 1000
}
```

**## src/main.rs**

```rust
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
```

**## Lancement**

```bash
cargo run
```

**## Résultat**

```text
{"distance":1100,"name":"target-001"}
{"distance":1200,"name":"target-002"}
{"distance":1300,"name":"target-003"}
```

