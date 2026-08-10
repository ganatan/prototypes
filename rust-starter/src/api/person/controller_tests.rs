use axum::{
    body::{
        Body,
        to_bytes,
    },
    http::{
        Method,
        Request,
        Response,
        StatusCode,
        header::CONTENT_TYPE,
    },
};
use serde_json::{
    Value,
    json,
};
use tower::ServiceExt;

use super::controller::routes;

fn empty_request(
    method: Method,
    uri: &str,
) -> Request<Body> {
    Request::builder()
        .method(method)
        .uri(uri)
        .body(
            Body::empty(),
        )
        .unwrap()
}

fn json_request(
    method: Method,
    uri: &str,
    payload: Value,
) -> Request<Body> {
    Request::builder()
        .method(method)
        .uri(uri)
        .header(
            CONTENT_TYPE,
            "application/json",
        )
        .body(
            Body::from(
                payload.to_string(),
            ),
        )
        .unwrap()
}

async fn read_json(
    response: Response<Body>,
) -> Value {
    let body = to_bytes(
        response.into_body(),
        usize::MAX,
    )
    .await
    .unwrap();

    serde_json::from_slice(
        &body,
    )
    .unwrap()
}

#[tokio::test]
async fn get_all_persons_should_return_seven_persons() {
    let response = routes()
        .oneshot(
            empty_request(
                Method::GET,
                "/persons",
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::OK,
    );

    let body =
        read_json(response).await;

    assert_eq!(
        body.as_array()
            .unwrap()
            .len(),
        7,
    );
}

#[tokio::test]
async fn get_person_by_id_should_return_person_output() {
    let response = routes()
        .oneshot(
            empty_request(
                Method::GET,
                "/persons/1",
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::OK,
    );

    let body =
        read_json(response).await;

    assert_eq!(
        body["id"],
        1,
    );

    assert_eq!(
        body["firstName"],
        "Steven",
    );

    assert_eq!(
        body["lastName"],
        "Spielberg",
    );

    assert_eq!(
        body["cityId"],
        1,
    );
}

#[tokio::test]
async fn create_person_should_return_created_person() {
    let response = routes()
        .oneshot(
            json_request(
                Method::POST,
                "/persons",
                json!({
                    "firstName": "Clint",
                    "lastName": "Eastwood",
                    "cityId": 8
                }),
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::CREATED,
    );

    let body =
        read_json(response).await;

    assert_eq!(
        body["id"],
        8,
    );

    assert_eq!(
        body["firstName"],
        "Clint",
    );

    assert_eq!(
        body["lastName"],
        "Eastwood",
    );

    assert_eq!(
        body["cityId"],
        8,
    );
}

#[tokio::test]
async fn create_person_should_reject_invalid_payload() {
    let response = routes()
        .oneshot(
            json_request(
                Method::POST,
                "/persons",
                json!({
                    "firstName": "",
                    "lastName": "Eastwood",
                    "cityId": 0
                }),
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::BAD_REQUEST,
    );
}

#[tokio::test]
async fn update_person_should_return_updated_person() {
    let response = routes()
        .oneshot(
            json_request(
                Method::PUT,
                "/persons/1",
                json!({
                    "firstName": "Steven",
                    "lastName": "Spielberg Updated",
                    "cityId": 10
                }),
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::OK,
    );

    let body =
        read_json(response).await;

    assert_eq!(
        body["id"],
        1,
    );

    assert_eq!(
        body["lastName"],
        "Spielberg Updated",
    );

    assert_eq!(
        body["cityId"],
        10,
    );
}

#[tokio::test]
async fn delete_person_should_return_no_content() {
    let response = routes()
        .oneshot(
            empty_request(
                Method::DELETE,
                "/persons/1",
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::NO_CONTENT,
    );
}

#[tokio::test]
async fn get_person_by_id_should_return_not_found() {
    let response = routes()
        .oneshot(
            empty_request(
                Method::GET,
                "/persons/999",
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::NOT_FOUND,
    );
}