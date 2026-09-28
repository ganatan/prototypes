# Projet Rust CRUD

## Création

```bash
cargo new rust-crud
```

## Dépendances

```bash
cargo add axum
cargo add tokio --features full
cargo add serde --features derive
cargo add serde_json
```

## Cargo.toml

```toml
[package]
name = "rust-crud"
version = "0.1.0"
edition = "2024"

[dependencies]
axum = "0.8"
serde = { version = "1", features = ["derive"] }
serde_json = "1"
tokio = { version = "1", features = ["full"] }
```

## main.rs

```rust
use axum::{routing::get, Router};

#[tokio::main]
async fn main() {
    let app = Router::new().route("/", get(root));
    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await.unwrap();

    println!("http://localhost:3000");

    axum::serve(listener, app).await.unwrap();
}

async fn root() -> &'static str {
    "Rust backend"
}
```

## Exécution

```bash
cargo run                          # Compile et lance
http://localhost:3000              # Teste l'application
cargo build                        # Compile dans target/debug
.\target\debug\rust-crud.exe       # Lance sous Windows
cargo build --release              # Compile dans target/release
```