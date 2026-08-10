mod api;
mod config;

use std::env;

use api::{person, root};
use axum::Router;
use config::{DATABASE_URL_KEY, SERVER_ADDRESS};
use sqlx::postgres::PgPoolOptions;
use tokio::net::TcpListener;

#[tokio::main]
async fn main() {
    dotenvy::dotenv().ok();

    let database_url = env::var(DATABASE_URL_KEY).expect("DATABASE_URL absent");

    let pool = PgPoolOptions::new()
        .max_connections(5)
        .connect(&database_url)
        .await
        .expect("Connexion à la database impossible");

    println!("Connexion à la database réussie");

    let persons =
        sqlx::query_as::<_, (i32, String, String, i32)>("SELECT * FROM person ORDER BY id")
            .fetch_all(&pool)
            .await
            .expect("Lecture de la table person impossible");

    println!("Persons trouvées : {}", persons.len());

    for person in persons {
        println!(
            "{} - {} {} - city_id: {}",
            person.0, person.1, person.2, person.3
        );
    }

    let app = Router::new()
        .merge(root::controller::routes())
        .merge(person::controller::routes(pool));

    let listener = TcpListener::bind(SERVER_ADDRESS)
        .await
        .expect("Impossible de démarrer le serveur");

    println!("Application disponible sur http://localhost:3000");

    axum::serve(listener, app)
        .await
        .expect("Erreur pendant l'exécution du serveur");
}
