use std::collections::HashMap;

use axum::{Json, Router, routing::get};
use serde::Serialize;
use serde_json::{Value, json};

use crate::config::{APPLICATION_NAME, APPLICATION_STATUS, RUST_EDITION};

#[derive(Serialize)]
pub struct ApiInfo {
    application: &'static str,
    status: &'static str,
    rust: &'static str,
}

pub fn routes() -> Router {
    Router::new()
        .route("/", get(root))
        .route("/info", get(root_with_struct))
        .route("/status", get(root_with_hash_map))
}

async fn root() -> Json<Value> {
    Json(json!({
        "application": APPLICATION_NAME,
        "status": APPLICATION_STATUS,
        "rust": RUST_EDITION
    }))
}

async fn root_with_struct() -> Json<ApiInfo> {
    Json(ApiInfo {
        application: APPLICATION_NAME,
        status: APPLICATION_STATUS,
        rust: RUST_EDITION,
    })
}

async fn root_with_hash_map() -> Json<HashMap<&'static str, &'static str>> {
    let mut response = HashMap::new();

    response.insert("application", APPLICATION_NAME);
    response.insert("status", APPLICATION_STATUS);
    response.insert("rust", RUST_EDITION);

    Json(response)
}
