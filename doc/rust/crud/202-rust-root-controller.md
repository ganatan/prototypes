**## Configuration Rust**

Créer un fichier `.env` à la racine :

```env
APPLICATION_NAME=rust-crud
SERVER_ADDRESS=127.0.0.1
SERVER_PORT=3000
```

**## Projet**

```bash
cargo new rust-crud
cd rust-crud
```

**## Configuration Cargo**

`Cargo.toml` :

```toml
[package]
name = "rust-crud"
version = "1.0.0"
edition = "2024"

[dependencies]
axum = "0.8"
dotenvy = "0.15"
serde = { version = "1", features = ["derive"] }
tokio = { version = "1", features = ["full"] }
```

**## src/main.rs**

```rust
use std::env;

use axum::{routing::get, Json, Router};
use dotenvy::dotenv;
use serde::Serialize;

#[derive(Serialize)]
struct ApiInfo {
    application: String,
    status: String,
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let application = env::var("APPLICATION_NAME").unwrap_or_else(|_| "rust-crud".to_string());
    let address = env::var("SERVER_ADDRESS").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("SERVER_PORT").unwrap_or_else(|_| "3000".to_string());

    let app = Router::new().route("/", get(root)).with_state(application);
    let listener = tokio::net::TcpListener::bind(format!("{address}:{port}")).await.unwrap();

    axum::serve(listener, app).await.unwrap();
}

async fn root(axum::extract::State(application): axum::extract::State<String>) -> Json<ApiInfo> {
    Json(ApiInfo {
        application,
        status: "running".to_string(),
    })
}
```

**## Exécution**

```bash
cargo check
cargo run
```

**## Vérifier l'API**

```text
GET http://localhost:3000/
```

Résultat :

```json
{
  "application": "rust-crud",
  "status": "running"
}
```

**## Build**

```bash
cargo build --release
```

Windows :

```text
target/release/rust-crud.exe
```

Linux :

```text
target/release/rust-crud
```