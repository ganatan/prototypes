use std::env;

use axum::{
    Router,
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
use sqlx::{
    PgPool,
    postgres::PgPoolOptions,
};
use tower::ServiceExt;

use super::controller::routes;

async fn create_pool() -> PgPool {
    dotenvy::dotenv().ok();

    let database_url =
        env::var(
            "DATABASE_URL",
        )
        .expect(
            "DATABASE_URL absent",
        );

    PgPoolOptions::new()
        .max_connections(5)
        .connect(
            &database_url,
        )
        .await
        .expect(
            "Connexion database impossible",
        )
}

async fn create_app() -> (
    Router,
    PgPool,
) {
    let pool =
        create_pool().await;

    let app =
        routes(
            pool.clone(),
        );

    (
        app,
        pool,
    )
}

async fn insert_person(
    pool: &PgPool,
    first_name: &str,
    last_name: &str,
    city_id: i32,
) -> i32 {
    sqlx::query_scalar::<
        _,
        i32,
    >(
        r#"
        INSERT INTO person (
            first_name,
            last_name,
            city_id
        )
        VALUES (
            $1,
            $2,
            $3
        )
        RETURNING id
        "#,
    )
    .bind(first_name)
    .bind(last_name)
    .bind(city_id)
    .fetch_one(pool)
    .await
    .unwrap()
}

async fn delete_person(
    pool: &PgPool,
    id: i32,
) {
    sqlx::query(
        r#"
        DELETE FROM person
        WHERE id = $1
        "#,
    )
    .bind(id)
    .execute(pool)
    .await
    .unwrap();
}

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
    let body =
        to_bytes(
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
async fn get_all_persons_should_return_persons() {
    let (
        app,
        pool,
    ) = create_app().await;

    let id =
        insert_person(
            &pool,
            "Test",
            "GetAll",
            100,
        )
        .await;

    let response = app
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

    let persons =
        body.as_array()
            .unwrap();

    assert!(
        persons
            .iter()
            .any(
                |person| {
                    person["id"]
                        == id
                },
            ),
    );

    delete_person(
        &pool,
        id,
    )
    .await;
}

#[tokio::test]
async fn get_person_by_id_should_return_person_output() {
    let (
        app,
        pool,
    ) = create_app().await;

    let id =
        insert_person(
            &pool,
            "Test",
            "GetById",
            101,
        )
        .await;

    let uri =
        format!(
            "/persons/{id}"
        );

    let response = app
        .oneshot(
            empty_request(
                Method::GET,
                &uri,
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
        id,
    );

    assert_eq!(
        body["firstName"],
        "Test",
    );

    assert_eq!(
        body["lastName"],
        "GetById",
    );

    assert_eq!(
        body["cityId"],
        101,
    );

    delete_person(
        &pool,
        id,
    )
    .await;
}

#[tokio::test]
async fn create_person_should_return_created_person() {
    let (
        app,
        pool,
    ) = create_app().await;

    let response = app
        .oneshot(
            json_request(
                Method::POST,
                "/persons",
                json!({
                    "firstName": "Clint",
                    "lastName": "Eastwood",
                    "cityId": 102
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

    let id =
        body["id"]
            .as_i64()
            .unwrap()
            as i32;

    assert!(
        id > 0,
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
        102,
    );

    delete_person(
        &pool,
        id,
    )
    .await;
}

#[tokio::test]
async fn create_person_should_reject_invalid_payload() {
    let (
        app,
        _,
    ) = create_app().await;

    let response = app
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
    let (
        app,
        pool,
    ) = create_app().await;

    let id =
        insert_person(
            &pool,
            "Test",
            "BeforeUpdate",
            103,
        )
        .await;

    let uri =
        format!(
            "/persons/{id}"
        );

    let response = app
        .oneshot(
            json_request(
                Method::PUT,
                &uri,
                json!({
                    "firstName": "Test",
                    "lastName": "AfterUpdate",
                    "cityId": 104
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
        id,
    );

    assert_eq!(
        body["firstName"],
        "Test",
    );

    assert_eq!(
        body["lastName"],
        "AfterUpdate",
    );

    assert_eq!(
        body["cityId"],
        104,
    );

    delete_person(
        &pool,
        id,
    )
    .await;
}

#[tokio::test]
async fn delete_person_should_return_no_content() {
    let (
        app,
        pool,
    ) = create_app().await;

    let id =
        insert_person(
            &pool,
            "Test",
            "Delete",
            105,
        )
        .await;

    let uri =
        format!(
            "/persons/{id}"
        );

    let response = app
        .oneshot(
            empty_request(
                Method::DELETE,
                &uri,
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::NO_CONTENT,
    );

    let count =
        sqlx::query_scalar::<
            _,
            i64,
        >(
            r#"
            SELECT COUNT(*)
            FROM person
            WHERE id = $1
            "#,
        )
        .bind(id)
        .fetch_one(
            &pool,
        )
        .await
        .unwrap();

    assert_eq!(
        count,
        0,
    );
}

#[tokio::test]
async fn get_person_by_id_should_return_not_found() {
    let (
        app,
        _,
    ) = create_app().await;

    let response = app
        .oneshot(
            empty_request(
                Method::GET,
                "/persons/2147483647",
            ),
        )
        .await
        .unwrap();

    assert_eq!(
        response.status(),
        StatusCode::NOT_FOUND,
    );
}

// use axum::{
//     body::{
//         Body,
//         to_bytes,
//     },
//     http::{
//         Method,
//         Request,
//         Response,
//         StatusCode,
//         header::CONTENT_TYPE,
//     },
// };
// use serde_json::{
//     Value,
//     json,
// };
// use tower::ServiceExt;

// use super::controller::routes;

// fn empty_request(
//     method: Method,
//     uri: &str,
// ) -> Request<Body> {
//     Request::builder()
//         .method(method)
//         .uri(uri)
//         .body(
//             Body::empty(),
//         )
//         .unwrap()
// }

// fn json_request(
//     method: Method,
//     uri: &str,
//     payload: Value,
// ) -> Request<Body> {
//     Request::builder()
//         .method(method)
//         .uri(uri)
//         .header(
//             CONTENT_TYPE,
//             "application/json",
//         )
//         .body(
//             Body::from(
//                 payload.to_string(),
//             ),
//         )
//         .unwrap()
// }

// async fn read_json(
//     response: Response<Body>,
// ) -> Value {
//     let body = to_bytes(
//         response.into_body(),
//         usize::MAX,
//     )
//     .await
//     .unwrap();

//     serde_json::from_slice(
//         &body,
//     )
//     .unwrap()
// }

// #[tokio::test]
// async fn get_all_persons_should_return_seven_persons() {
//     let response = routes()
//         .oneshot(
//             empty_request(
//                 Method::GET,
//                 "/persons",
//             ),
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         response.status(),
//         StatusCode::OK,
//     );

//     let body =
//         read_json(response).await;

//     assert_eq!(
//         body.as_array()
//             .unwrap()
//             .len(),
//         7,
//     );
// }

// #[tokio::test]
// async fn get_person_by_id_should_return_person_output() {
//     let response = routes()
//         .oneshot(
//             empty_request(
//                 Method::GET,
//                 "/persons/1",
//             ),
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         response.status(),
//         StatusCode::OK,
//     );

//     let body =
//         read_json(response).await;

//     assert_eq!(
//         body["id"],
//         1,
//     );

//     assert_eq!(
//         body["firstName"],
//         "Steven",
//     );

//     assert_eq!(
//         body["lastName"],
//         "Spielberg",
//     );

//     assert_eq!(
//         body["cityId"],
//         1,
//     );
// }

// #[tokio::test]
// async fn create_person_should_return_created_person() {
//     let response = routes()
//         .oneshot(
//             json_request(
//                 Method::POST,
//                 "/persons",
//                 json!({
//                     "firstName": "Clint",
//                     "lastName": "Eastwood",
//                     "cityId": 8
//                 }),
//             ),
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         response.status(),
//         StatusCode::CREATED,
//     );

//     let body =
//         read_json(response).await;

//     assert_eq!(
//         body["id"],
//         8,
//     );

//     assert_eq!(
//         body["firstName"],
//         "Clint",
//     );

//     assert_eq!(
//         body["lastName"],
//         "Eastwood",
//     );

//     assert_eq!(
//         body["cityId"],
//         8,
//     );
// }

// #[tokio::test]
// async fn create_person_should_reject_invalid_payload() {
//     let response = routes()
//         .oneshot(
//             json_request(
//                 Method::POST,
//                 "/persons",
//                 json!({
//                     "firstName": "",
//                     "lastName": "Eastwood",
//                     "cityId": 0
//                 }),
//             ),
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         response.status(),
//         StatusCode::BAD_REQUEST,
//     );
// }

// #[tokio::test]
// async fn update_person_should_return_updated_person() {
//     let response = routes()
//         .oneshot(
//             json_request(
//                 Method::PUT,
//                 "/persons/1",
//                 json!({
//                     "firstName": "Steven",
//                     "lastName": "Spielberg Updated",
//                     "cityId": 10
//                 }),
//             ),
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         response.status(),
//         StatusCode::OK,
//     );

//     let body =
//         read_json(response).await;

//     assert_eq!(
//         body["id"],
//         1,
//     );

//     assert_eq!(
//         body["lastName"],
//         "Spielberg Updated",
//     );

//     assert_eq!(
//         body["cityId"],
//         10,
//     );
// }

// #[tokio::test]
// async fn delete_person_should_return_no_content() {
//     let response = routes()
//         .oneshot(
//             empty_request(
//                 Method::DELETE,
//                 "/persons/1",
//             ),
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         response.status(),
//         StatusCode::NO_CONTENT,
//     );
// }

// #[tokio::test]
// async fn get_person_by_id_should_return_not_found() {
//     let response = routes()
//         .oneshot(
//             empty_request(
//                 Method::GET,
//                 "/persons/999",
//             ),
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         response.status(),
//         StatusCode::NOT_FOUND,
//     );
// }