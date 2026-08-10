#[derive(
    Clone,
    Debug,
    Eq,
    PartialEq,
)]
pub struct Person {
    pub id: u32,
    pub first_name: String,
    pub last_name: String,
    pub city_id: u32,
}