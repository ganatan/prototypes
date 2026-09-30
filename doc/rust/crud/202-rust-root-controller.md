**# RootController**

**## ⚙️ Configuration Rust**

Créer un fichier `.env` à la racine :

```env
APPLICATION_NAME=rust-crud
SERVER_ADDRESS=127.0.0.1
SERVER_PORT=3000
```

Valeurs utilisées par défaut si `.env` est absent :

```text
APPLICATION_NAME=rust-crud
SERVER_ADDRESS=127.0.0.1
SERVER_PORT=3000
```

**## Projet**

Créer le projet :

```bash
cargo new rust-crud
cd rust-crud
```

**## Configuration Cargo**

Fichier `Cargo.toml` :

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

**## Structure**

```text
rust-crud
├── .env
├── Cargo.toml
└── src
    └── main.rs
```

**## Implémentation**

### Fichier

`src/main.rs`

### Code

```rust
use std::env;

use axum::{
    routing::get,
    Json, Router,
};
use dotenvy::dotenv;
use serde::Serialize;

#[derive(Serialize)]
struct ApiInfo {
    application: String,
    status: String,
    rust: String,
}

#[tokio::main]
async fn main() {
    dotenv().ok();

    let application = env::var("APPLICATION_NAME").unwrap_or_else(|_| "rust-crud".to_string());
    let address = env::var("SERVER_ADDRESS").unwrap_or_else(|_| "127.0.0.1".to_string());
    let port = env::var("SERVER_PORT").unwrap_or_else(|_| "3000".to_string());

    let app = Router::new()
        .route("/", get(root))
        .route("/info", get(info))
        .route("/status", get(status))
        .with_state(application);

    let listener = tokio::net::TcpListener::bind(format!("{address}:{port}")).await.unwrap();

    axum::serve(listener, app).await.unwrap();
}

async fn root(
    axum::extract::State(application): axum::extract::State<String>,
) -> Json<ApiInfo> {
    Json(api_info(application))
}

async fn info(
    axum::extract::State(application): axum::extract::State<String>,
) -> Json<ApiInfo> {
    Json(api_info(application))
}

async fn status(
    axum::extract::State(application): axum::extract::State<String>,
) -> Json<ApiInfo> {
    Json(api_info(application))
}

fn api_info(application: String) -> ApiInfo {
    ApiInfo {
        application,
        status: "running".to_string(),
        rust: rust_version(),
    }
}

fn rust_version() -> String {
    option_env!("RUSTC_VERSION").unwrap_or("stable").to_string()
}
```

**## Version Rust**

Pour obtenir réellement la version du compilateur dans la réponse JSON, ajouter un script de build.

Créer :

```text
build.rs
```

Code :

```rust
use std::process::Command;

fn main() {
    let output = Command::new("rustc").arg("--version").output().unwrap();
    let version = String::from_utf8(output.stdout).unwrap();
    println!("cargo:rustc-env=RUSTC_VERSION={}", version.trim());
}
```

La structure devient :

```text
rust-crud
├── .env
├── build.rs
├── Cargo.toml
└── src
    └── main.rs
```

**## Build & Exécution**

```bash
cargo check
cargo test
cargo build
cargo run
```

Compiler en mode release :

```bash
cargo build --release
```

Exécutable généré sous Windows :

```text
target/release/rust-crud.exe
```

Exécutable généré sous Linux :

```text
target/release/rust-crud
```

Lancer directement sous Windows :

```bash
target\release\rust-crud.exe
```

Lancer directement sous Linux :

```bash
./target/release/rust-crud
```

**## Vérifier l'API**

```text
GET http://localhost:3000/
```

Résultat :

```json
{
  "application": "rust-crud",
  "status": "running",
  "rust": "rustc 1.x.x"
}
```

**## Info**

```text
GET http://localhost:3000/info
```

Résultat :

```json
{
  "application": "rust-crud",
  "status": "running",
  "rust": "rustc 1.x.x"
}
```

**## Status**

```text
GET http://localhost:3000/status
```

Résultat :

```json
{
  "application": "rust-crud",
  "status": "running",
  "rust": "rustc 1.x.x"
}
```

**## Commandes Cargo**

```bash
cargo check
cargo build
cargo build --release
cargo run
cargo test
cargo clean
cargo tree
cargo update
cargo fmt
cargo clippy
```