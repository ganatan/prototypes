## Projet rust-emitter

## Commandes essentielles

```bash
cargo check                  # Vérifier le code sans générer l'exécutable
cargo run                    # Compiler puis exécuter le projet
cargo build                  # Compiler en mode debug
cargo build --release        # Compiler en mode optimisé
```

## Exécutables générés

```text
target/debug/rust-starter.exe      # Windows - build debug
target/debug/rust-starter          # Linux - build debug
target/release/rust-starter.exe    # Windows - build release
target/release/rust-starter        # Linux - build release
```


## Dépendances

```toml
[dependencies]
serde = { version = "1", features = ["derive"] }
serde_json = "1"
tokio = { version = "1", features = ["full"] }
```

## src/main.rs

```rust
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
```

## Lancement

```bash
cargo run
```

## Résultat

```text
{"distance":1100,"name":"target-001"}
{"distance":1200,"name":"target-002"}
{"distance":1300,"name":"target-003"}
```
