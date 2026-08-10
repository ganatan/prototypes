mod api;
mod config;

use api::{
    person,
    root,
};
use axum::Router;
use config::SERVER_ADDRESS;
use tokio::net::TcpListener;

#[tokio::main]
async fn main() {
    let app = Router::new()
        .merge(
            root::controller::routes(),
        )
        .merge(
            person::controller::routes(),
        );

    let listener = TcpListener::bind(
        SERVER_ADDRESS,
    )
    .await
    .expect(
        "Impossible de démarrer le serveur",
    );

    println!(
        "Application disponible sur http://localhost:3000"
    );

    axum::serve(listener, app)
        .await
        .expect(
            "Erreur pendant l'exécution du serveur",
        );
}