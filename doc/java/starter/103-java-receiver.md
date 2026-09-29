**## Projet rust-receiver**

**## Commandes essentielles**

```bash
cargo check                  # Vérifier le code sans générer l'exécutable
cargo run                    # Compiler puis exécuter le projet
cargo build                  # Compiler en mode debug
cargo build --release        # Compiler en mode optimisé
```

**## Exécutables générés**

```text
target/debug/rust-receiver.exe      # Windows - build debug
target/debug/rust-receiver          # Linux - build debug
target/release/rust-receiver.exe    # Windows - build release
target/release/rust-receiver        # Linux - build release
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
  "address": "127.0.0.1:5000"
}
```

**## src/main.rs**

```rust
use serde::Deserialize;
use std::fs;
use tokio::net::UdpSocket;

#[derive(Deserialize)]
struct Config {
    address: String,
}

#[tokio::main]
async fn main() {
    let config: Config = serde_json::from_str(&fs::read_to_string("config.json").unwrap()).unwrap();
    let socket = UdpSocket::bind(&config.address).await.unwrap();
    let mut buffer = [0u8; 2048];

    loop {
        let (size, source) = socket.recv_from(&mut buffer).await.unwrap();
        let message = String::from_utf8_lossy(&buffer[..size]);

        println!("UDP received from {} on {} -> {}", source, config.address, message);
    }
}
```

**## Lancement**

```bash
cargo run
```

**## Résultat**

```text
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1100,"name":"target-001"}
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1200,"name":"target-002"}
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1300,"name":"target-003"}
```

