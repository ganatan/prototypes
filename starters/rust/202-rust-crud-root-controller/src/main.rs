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