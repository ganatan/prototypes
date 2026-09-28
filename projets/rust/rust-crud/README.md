# Rust Backend

Backend REST développé en Rust avec Axum, Tokio, SQLx et PostgreSQL.

---

## Technologies

* Rust
* Axum
* Tokio
* SQLx
* PostgreSQL
* Serde

---

## Structure du projet

```text
rust-starter/
├── src/
│   ├── api/
│   │   ├── person/
│   │   │   ├── controller_tests.rs
│   │   │   ├── controller.rs
│   │   │   ├── mod.rs
│   │   │   ├── person_input_dto.rs
│   │   │   ├── person_output_dto.rs
│   │   │   ├── person.rs
│   │   │   ├── repository_tests.rs
│   │   │   ├── repository.rs
│   │   │   ├── service_tests.rs
│   │   │   └── service.rs
│   │   ├── root/
│   │   └── mod.rs
│   ├── config.rs
│   └── main.rs
├── .env
├── .gitignore
├── Cargo.lock
└── Cargo.toml
```

---

## Architecture

```text
HTTP Request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

Les données entrantes et sortantes passent par des DTO :

```text
JSON
    ↓
PersonInputDto
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
Person
    ↓
PersonOutputDto
    ↓
JSON
```

---

## Configuration PostgreSQL

Créer un fichier :

```text
.env
```

avec :

```env
DATABASE_URL=postgresql://postgres:password@localhost:5432/database
```

Adapter :

```text
postgres
password
database
```

à la configuration PostgreSQL locale.

---

## Installation des dépendances

Cargo installe automatiquement les dépendances définies dans :

```text
Cargo.toml
```

Pour télécharger et compiler les dépendances :

```bash
cargo build
```

---

## Lancement en développement

```bash
cargo run
```

L'application est disponible sur :

```text
http://localhost:3000
```

---

## Compilation Release

Pour compiler une version optimisée :

```bash
cargo build --release
```

L'exécutable est généré dans :

```text
target/release/
```

---

## Lancement en Release

### Windows

```powershell
.\target\release\rust-starter.exe
```

### Linux

```bash
./target/release/rust-starter
```

La version `release` utilise les optimisations du compilateur Rust et doit être privilégiée pour les benchmarks et les tests de performance.

---

## Tests

Lancer tous les tests :

```bash
cargo test
```

Les tests sont séparés par couche :

```text
controller_tests.rs
service_tests.rs
repository_tests.rs
```

---

## API Person

Endpoints disponibles :

```text
GET    /persons
GET    /persons/{id}
POST   /persons
PUT    /persons/{id}
DELETE /persons/{id}
```

---

## Exemple

Récupérer toutes les personnes :

```http
GET http://localhost:3000/persons
```

Réponse :

```json
[
  {
    "id": 1,
    "firstName": "Steven",
    "lastName": "Spielberg",
    "cityId": 1
  }
]
```

---

## Commandes principales

```bash
cargo run
cargo build
cargo build --release
cargo test
```

Version de développement :

```bash
cargo run
```

Version optimisée :

```bash
cargo build --release
```

puis :

```powershell
.\target\release\rust-starter.exe
```
