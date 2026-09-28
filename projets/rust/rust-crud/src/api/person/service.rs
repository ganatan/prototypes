use super::{person::Person, repository::PersonRepository};

#[derive(Clone)]
pub struct PersonService {
    person_repository: PersonRepository,
}

impl PersonService {
    pub fn new(person_repository: PersonRepository) -> Self {
        Self { person_repository }
    }

    pub async fn get_all(&self) -> Result<Vec<Person>, sqlx::Error> {
        self.person_repository.find_all().await
    }

    pub async fn get_by_id(&self, id: i32) -> Result<Option<Person>, sqlx::Error> {
        self.person_repository.find_by_id(id).await
    }

    pub async fn create(
        &self,
        first_name: String,
        last_name: String,
        city_id: i32,
    ) -> Result<Person, sqlx::Error> {
        self.person_repository
            .create(first_name, last_name, city_id)
            .await
    }

    pub async fn update(
        &self,
        id: i32,
        first_name: String,
        last_name: String,
        city_id: i32,
    ) -> Result<Option<Person>, sqlx::Error> {
        self.person_repository
            .update(id, first_name, last_name, city_id)
            .await
    }

    pub async fn delete(&self, id: i32) -> Result<bool, sqlx::Error> {
        self.person_repository.delete(id).await
    }
}
