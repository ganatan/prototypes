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
target/debug/rust-emitter.exe
target/debug/rust-emitter
target/release/rust-emitter.exe
target/release/rust-emitter
```

**## Dépendances**

```toml
[dependencies]
dotenvy = "0.15"
serde = { version = "1", features = ["derive"] }
serde_json = "1"
tokio = { version = "1", features = ["full"] }
```

**## Configuration locale**

Créer `.env` à la racine :

```env
UDP_ADDRESS=127.0.0.1:5000
UDP_INTERVAL_MS=1000
```

**## src/main.rs**

```rust
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
```

**## Lancement**

```bash
cargo run
```

**## Résultat**

```text
UDP sent to 127.0.0.1:5000 -> {"distance":1100,"name":"target-001"}
UDP sent to 127.0.0.1:5000 -> {"distance":1200,"name":"target-002"}
UDP sent to 127.0.0.1:5000 -> {"distance":1300,"name":"target-003"}
```

**## Configuration CI/CD**

En local :

```text
.env
→ variables d'environnement
→ rust-emitter
```

Avec Docker / OpenShift :

```text
ConfigMap / Secret
→ variables d'environnement
→ rust-emitter
```

Le fichier `.env` est utilisé uniquement en local.

En CI/CD, les variables sont injectées par l'environnement :

```text
UDP_ADDRESS
UDP_INTERVAL_MS
```