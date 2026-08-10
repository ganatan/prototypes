use super::{
    repository::PersonRepository,
    service::PersonService,
};

fn create_service() -> PersonService {
    let repository =
        PersonRepository::new();

    PersonService::new(
        repository,
    )
}

#[tokio::test]
async fn get_all_should_return_seven_persons() {
    let service =
        create_service();

    let persons =
        service.get_all().await;

    assert_eq!(
        persons.len(),
        7,
    );
}

#[tokio::test]
async fn get_by_id_should_return_person() {
    let service =
        create_service();

    let person = service
        .get_by_id(1)
        .await
        .unwrap();

    assert_eq!(
        person.id,
        1,
    );

    assert_eq!(
        person.first_name,
        "Steven",
    );

    assert_eq!(
        person.last_name,
        "Spielberg",
    );
}

#[tokio::test]
async fn get_by_id_should_return_none() {
    let service =
        create_service();

    let person = service
        .get_by_id(999)
        .await;

    assert_eq!(
        person,
        None,
    );
}

#[tokio::test]
async fn create_should_add_person() {
    let service =
        create_service();

    let person = service
        .create(
            "Clint".to_string(),
            "Eastwood".to_string(),
            8,
        )
        .await;

    assert_eq!(
        person.id,
        8,
    );

    assert_eq!(
        person.first_name,
        "Clint",
    );

    assert_eq!(
        person.last_name,
        "Eastwood",
    );
}

#[tokio::test]
async fn update_should_modify_person() {
    let service =
        create_service();

    let person = service
        .update(
            1,
            "Steven".to_string(),
            "Spielberg Updated".to_string(),
            10,
        )
        .await
        .unwrap();

    assert_eq!(
        person.id,
        1,
    );

    assert_eq!(
        person.last_name,
        "Spielberg Updated",
    );

    assert_eq!(
        person.city_id,
        10,
    );
}

#[tokio::test]
async fn delete_should_remove_person() {
    let service =
        create_service();

    let deleted =
        service.delete(1).await;

    assert!(
        deleted,
    );

    let person = service
        .get_by_id(1)
        .await;

    assert_eq!(
        person,
        None,
    );
}