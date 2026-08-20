use std::env;

use sqlx::{
    PgPool,
    postgres::PgPoolOptions,
};

use super::repository::PersonRepository;

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

async fn create_repository() -> PersonRepository {
    let pool =
        create_pool().await;

    PersonRepository::new(
        pool,
    )
}

#[tokio::test]
async fn find_all_should_return_persons() {
    let repository =
        create_repository().await;

    let person = repository
        .create(
            "Test".to_string(),
            "FindAll".to_string(),
            101,
        )
        .await
        .unwrap();

    let persons = repository
        .find_all()
        .await
        .unwrap();

    assert!(
        persons
            .iter()
            .any(
                |item| {
                    item.id == person.id
                },
            ),
    );

    repository
        .delete(
            person.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn find_by_id_should_return_person() {
    let repository =
        create_repository().await;

    let created = repository
        .create(
            "Test".to_string(),
            "FindById".to_string(),
            102,
        )
        .await
        .unwrap();

    let person = repository
        .find_by_id(
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
        "FindById",
    );

    assert_eq!(
        person.city_id,
        102,
    );

    repository
        .delete(
            created.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn find_by_id_should_return_none() {
    let repository =
        create_repository().await;

    let person = repository
        .find_by_id(
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
    let repository =
        create_repository().await;

    let person = repository
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

    let stored_person = repository
        .find_by_id(
            person.id,
        )
        .await
        .unwrap()
        .unwrap();

    assert_eq!(
        stored_person,
        person,
    );

    repository
        .delete(
            person.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn update_should_modify_person() {
    let repository =
        create_repository().await;

    let created = repository
        .create(
            "Test".to_string(),
            "BeforeUpdate".to_string(),
            104,
        )
        .await
        .unwrap();

    let person = repository
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

    let stored_person = repository
        .find_by_id(
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

    repository
        .delete(
            created.id,
        )
        .await
        .unwrap();
}

#[tokio::test]
async fn update_should_return_none() {
    let repository =
        create_repository().await;

    let person = repository
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
    let repository =
        create_repository().await;

    let created = repository
        .create(
            "Test".to_string(),
            "Delete".to_string(),
            107,
        )
        .await
        .unwrap();

    let deleted = repository
        .delete(
            created.id,
        )
        .await
        .unwrap();

    assert!(
        deleted,
    );

    let person = repository
        .find_by_id(
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
    let repository =
        create_repository().await;

    let deleted = repository
        .delete(
            i32::MAX,
        )
        .await
        .unwrap();

    assert!(
        !deleted,
    );
}

// use super::repository::PersonRepository;

// #[tokio::test]
// async fn find_all_should_return_seven_persons() {
//     let repository =
//         PersonRepository::new();

//     let persons =
//         repository.find_all().await;

//     assert_eq!(
//         persons.len(),
//         7,
//     );
// }

// #[tokio::test]
// async fn find_by_id_should_return_person() {
//     let repository =
//         PersonRepository::new();

//     let person = repository
//         .find_by_id(1)
//         .await
//         .unwrap();

//     assert_eq!(
//         person.id,
//         1,
//     );

//     assert_eq!(
//         person.first_name,
//         "Steven",
//     );

//     assert_eq!(
//         person.last_name,
//         "Spielberg",
//     );

//     assert_eq!(
//         person.city_id,
//         1,
//     );
// }

// #[tokio::test]
// async fn find_by_id_should_return_none() {
//     let repository =
//         PersonRepository::new();

//     let person = repository
//         .find_by_id(999)
//         .await;

//     assert_eq!(
//         person,
//         None,
//     );
// }

// #[tokio::test]
// async fn create_should_add_person() {
//     let repository =
//         PersonRepository::new();

//     let person = repository
//         .create(
//             "Clint".to_string(),
//             "Eastwood".to_string(),
//             8,
//         )
//         .await;

//     assert_eq!(
//         person.id,
//         8,
//     );

//     assert_eq!(
//         person.first_name,
//         "Clint",
//     );

//     assert_eq!(
//         person.last_name,
//         "Eastwood",
//     );

//     assert_eq!(
//         person.city_id,
//         8,
//     );

//     let persons =
//         repository.find_all().await;

//     assert_eq!(
//         persons.len(),
//         8,
//     );
// }

// #[tokio::test]
// async fn update_should_modify_person() {
//     let repository =
//         PersonRepository::new();

//     let person = repository
//         .update(
//             1,
//             "Steven".to_string(),
//             "Spielberg Updated".to_string(),
//             10,
//         )
//         .await
//         .unwrap();

//     assert_eq!(
//         person.id,
//         1,
//     );

//     assert_eq!(
//         person.first_name,
//         "Steven",
//     );

//     assert_eq!(
//         person.last_name,
//         "Spielberg Updated",
//     );

//     assert_eq!(
//         person.city_id,
//         10,
//     );
// }

// #[tokio::test]
// async fn update_should_return_none() {
//     let repository =
//         PersonRepository::new();

//     let person = repository
//         .update(
//             999,
//             "Unknown".to_string(),
//             "Person".to_string(),
//             1,
//         )
//         .await;

//     assert_eq!(
//         person,
//         None,
//     );
// }

// #[tokio::test]
// async fn delete_should_remove_person() {
//     let repository =
//         PersonRepository::new();

//     let deleted =
//         repository.delete(1).await;

//     assert!(
//         deleted,
//     );

//     let person = repository
//         .find_by_id(1)
//         .await;

//     assert_eq!(
//         person,
//         None,
//     );
// }

// #[tokio::test]
// async fn delete_should_return_false() {
//     let repository =
//         PersonRepository::new();

//     let deleted =
//         repository.delete(999).await;

//     assert!(
//         !deleted,
//     );
// }