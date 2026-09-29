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
dotenvy = "0.15"
tokio = { version = "1", features = ["full"] }
```

**## Configuration locale**

Créer `.env` à la racine :

```env
UDP_ADDRESS=127.0.0.1:5000
```

Le fichier `.env` est optionnel.

Valeur utilisée par défaut :

```text
UDP_ADDRESS=127.0.0.1:5000
```

**## src/main.rs**

```rust
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
```

**## Lancement**

```bash
cargo run
```

ou après compilation :

```bash
cargo build --release
target\release\rust-receiver.exe
```

**## Résultat**

```text
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1100,"name":"target-001"}
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1200,"name":"target-002"}
UDP received from 127.0.0.1:54321 on 127.0.0.1:5000 -> {"distance":1300,"name":"target-003"}
```

**## Configuration CI/CD**

Priorité de configuration :

```text
variables d'environnement
→ .env
→ valeur par défaut
```

En local :

```text
.env
→ variables d'environnement
→ rust-receiver
```

Avec Docker / OpenShift :

```text
ConfigMap / Secret
→ variables d'environnement
→ rust-receiver
```

Le fichier `.env` est utilisé uniquement en local.

En CI/CD, la variable est injectée par l'environnement :

```text
UDP_ADDRESS
```