use std::env;

use sqlx::postgres::PgPoolOptions;

use super::{
    repository::PersonRepository,
    service::PersonService,
};

async fn create_service() -> PersonService {
    dotenvy::dotenv().ok();

    let database_url =
        env::var(
            "DATABASE_URL",
        )
        .expect(
            "DATABASE_URL absent",
        );

    let pool =
        PgPoolOptions::new()
            .max_connections(5)
            .connect(
                &database_url,
            )
            .await
            .expect(
                "Connexion database impossible",
            );

    let repository =
        PersonRepository::new(
            pool,
        );

    PersonService::new(
        repository,
    )
}

#[tokio::test]
async fn get_all_should_return_persons() {
    let service =
        create_service().await;

    let created = service
        .create(
            "Test".to_string(),
            "GetAll".to_string(),
            101,
        )
        .await
        .unwrap();

    let persons = service
        .get_all()
        .await
        .unwrap();

    assert!(
        persons
            .iter()
            .any(
                |person| {
                    person.id == created.id
                },
            ),
    );

    service
        .delete(
            created.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn get_by_id_should_return_person() {
    let service =
        create_service().await;

    let created = service
        .create(
            "Test".to_string(),
            "GetById".to_string(),
            102,
        )
        .await
        .unwrap();

    let person = service
        .get_by_id(
            created.id,
        )
        .await
        .unwrap()
        .unwrap();

    assert_eq!(
        person.id,
        created.id,
    );

    assert_eq!(
        person.first_name,
        "Test",
    );

    assert_eq!(
        person.last_name,
        "GetById",
    );

    assert_eq!(
        person.city_id,
        102,
    );

    service
        .delete(
            created.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn get_by_id_should_return_none() {
    let service =
        create_service().await;

    let person = service
        .get_by_id(
            i32::MAX,
        )
        .await
        .unwrap();

    assert_eq!(
        person,
        None,
    );
}

#[tokio::test]
async fn create_should_add_person() {
    let service =
        create_service().await;

    let person = service
        .create(
            "Clint".to_string(),
            "Eastwood".to_string(),
            103,
        )
        .await
        .unwrap();

    assert!(
        person.id > 0,
    );

    assert_eq!(
        person.first_name,
        "Clint",
    );

    assert_eq!(
        person.last_name,
        "Eastwood",
    );

    assert_eq!(
        person.city_id,
        103,
    );

    let stored_person = service
        .get_by_id(
            person.id,
        )
        .await
        .unwrap()
        .unwrap();

    assert_eq!(
        stored_person,
        person,
    );

    service
        .delete(
            person.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn update_should_modify_person() {
    let service =
        create_service().await;

    let created = service
        .create(
            "Test".to_string(),
            "BeforeUpdate".to_string(),
            104,
        )
        .await
        .unwrap();

    let person = service
        .update(
            created.id,
            "Test".to_string(),
            "AfterUpdate".to_string(),
            105,
        )
        .await
        .unwrap()
        .unwrap();

    assert_eq!(
        person.id,
        created.id,
    );

    assert_eq!(
        person.first_name,
        "Test",
    );

    assert_eq!(
        person.last_name,
        "AfterUpdate",
    );

    assert_eq!(
        person.city_id,
        105,
    );

    let stored_person = service
        .get_by_id(
            created.id,
        )
        .await
        .unwrap()
        .unwrap();

    assert_eq!(
        stored_person.last_name,
        "AfterUpdate",
    );

    assert_eq!(
        stored_person.city_id,
        105,
    );

    service
        .delete(
            created.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn update_should_return_none() {
    let service =
        create_service().await;

    let person = service
        .update(
            i32::MAX,
            "Unknown".to_string(),
            "Person".to_string(),
            106,
        )
        .await
        .unwrap();

    assert_eq!(
        person,
        None,
    );
}

#[tokio::test]
async fn delete_should_remove_person() {
    let service =
        create_service().await;

    let created = service
        .create(
            "Test".to_string(),
            "Delete".to_string(),
            107,
        )
        .await
        .unwrap();

    let deleted = service
        .delete(
            created.id,
        )
        .await
        .unwrap();

    assert!(
        deleted,
    );

    let person = service
        .get_by_id(
            created.id,
        )
        .await
        .unwrap();

    assert_eq!(
        person,
        None,
    );
}

#[tokio::test]
async fn delete_should_return_false() {
    let service =
        create_service().await;

    let deleted = service
        .delete(
            i32::MAX,
        )
        .await
        .unwrap();

    assert!(
        !deleted,
    );
}