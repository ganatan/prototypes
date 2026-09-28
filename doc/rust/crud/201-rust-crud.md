# Projet Rust CRUD

---

## Création

```bash
cargo new rust-crud
```

## Dépendances

```bash
cargo add axum                     # Framework HTTP pour les routes REST
cargo add tokio --features full    # Runtime asynchrone utilisé par Axum
cargo add serde --features derive  # Sérialisation et désérialisation
cargo add serde_json               # Gestion du JSON
---

## Cargo.toml


```toml
[package]
name = "rust-starter"
version = "0.1.0"
edition = "2024"

[dependencies]
axum = "0.8"
serde = { version = "1", features = ["derive"] }
serde_json = "1"
tokio = { version = "1", features = ["full"] }
```


---

## Fichier main


```rust
use axum::{
    routing::get,
    Router,
};

#[tokio::main]
async fn main() {
    let app = Router::new()
        .route("/", get(root));

    let listener = tokio::net::TcpListener::bind(
        "0.0.0.0:3000",
    )
    .await
    .unwrap();

    axum::serve(listener, app)
        .await
        .unwrap();
}

async fn root() -> &'static str {
    "Rust backend"
}
```

---

## ▶️ Lancement du projet

```bash
cargo run
```


```text
http://localhost:3000
```

---

## 🔍 Vérification

Ouvrir dans le navigateur :

```text
http://localhost:3000
```

Réponse attendue :

```text
Rust backend
```

---

## 📦 Compilation du projet

### Compilation de développement

```bash
cargo build
```

Le programme est généré dans :

```text
target/debug
```

### Compilation de production

```bash
cargo build --release
```

Le programme optimisé est généré dans :

```text
target/release
```

---

## ▶️ Lancement de l’exécutable

### Windows

```powershell
.\target\release\rust-starter.exe
```

### Linux

```bash
./target/release/rust-starter
```


