use serde::Deserialize;

#[derive(
    Debug,
    Deserialize,
)]
#[serde(rename_all = "camelCase")]
pub struct PersonInputDto {
    pub first_name: String,
    pub last_name: String,
    pub city_id: u32,
}

impl PersonInputDto {
    pub fn validate_and_normalize(
        self,
    ) -> Result<Self, ()> {
        let first_name =
            self.first_name.trim().to_string();

        let last_name =
            self.last_name.trim().to_string();

        if first_name.is_empty()
            || first_name.chars().count() > 50
            || last_name.is_empty()
            || last_name.chars().count() > 50
            || self.city_id == 0
        {
            return Err(());
        }

        Ok(Self {
            first_name,
            last_name,
            city_id: self.city_id,
        })
    }
}