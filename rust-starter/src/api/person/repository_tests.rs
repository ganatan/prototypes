use super::repository::PersonRepository;

#[tokio::test]
async fn find_all_should_return_seven_persons() {
    let repository =
        PersonRepository::new();

    let persons =
        repository.find_all().await;

    assert_eq!(
        persons.len(),
        7,
    );
}

#[tokio::test]
async fn find_by_id_should_return_person() {
    let repository =
        PersonRepository::new();

    let person = repository
        .find_by_id(1)
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

    assert_eq!(
        person.city_id,
        1,
    );
}

#[tokio::test]
async fn find_by_id_should_return_none() {
    let repository =
        PersonRepository::new();

    let person = repository
        .find_by_id(999)
        .await;

    assert_eq!(
        person,
        None,
    );
}

#[tokio::test]
async fn create_should_add_person() {
    let repository =
        PersonRepository::new();

    let person = repository
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

    assert_eq!(
        person.city_id,
        8,
    );

    let persons =
        repository.find_all().await;

    assert_eq!(
        persons.len(),
        8,
    );
}

#[tokio::test]
async fn update_should_modify_person() {
    let repository =
        PersonRepository::new();

    let person = repository
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
        person.first_name,
        "Steven",
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
async fn update_should_return_none() {
    let repository =
        PersonRepository::new();

    let person = repository
        .update(
            999,
            "Unknown".to_string(),
            "Person".to_string(),
            1,
        )
        .await;

    assert_eq!(
        person,
        None,
    );
}

#[tokio::test]
async fn delete_should_remove_person() {
    let repository =
        PersonRepository::new();

    let deleted =
        repository.delete(1).await;

    assert!(
        deleted,
    );

    let person = repository
        .find_by_id(1)
        .await;

    assert_eq!(
        person,
        None,
    );
}

#[tokio::test]
async fn delete_should_return_false() {
    let repository =
        PersonRepository::new();

    let deleted =
        repository.delete(999).await;

    assert!(
        !deleted,
    );
}