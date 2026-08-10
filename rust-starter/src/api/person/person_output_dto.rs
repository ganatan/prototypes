use serde::Serialize;

use super::person::Person;

#[derive(Debug, Serialize)]
#[serde(rename_all = "camelCase")]
pub struct PersonOutputDto {
    pub id: i32,
    pub first_name: String,
    pub last_name: String,
    pub city_id: i32,
}

impl From<&Person> for PersonOutputDto {
    fn from(person: &Person) -> Self {
        Self {
            id: person.id,
            first_name: person.first_name.clone(),
            last_name: person.last_name.clone(),
            city_id: person.city_id,
        }
    }
}
