## Créer le projet

```bash
cargo new rust-starter
```

## Modifier Fichier `src/main.rs`

```rust
fn main() {
    println!("Rust Starter");
}
```

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

## Cargo.toml

```toml
[package]
name = "rust-starter"
version = "0.1.0"
edition = "2024"

[dependencies]
```