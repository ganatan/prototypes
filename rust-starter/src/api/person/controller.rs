use std::sync::{
    Arc,
    atomic::{
        AtomicU32,
        Ordering,
    },
};

use axum::{
    Json,
    Router,
    extract::{
        Path,
        State,
    },
    http::StatusCode,
    routing::get,
};
use tokio::sync::RwLock;

use super::{
    person::Person,
    person_input_dto::PersonInputDto,
    person_output_dto::PersonOutputDto,
};

#[derive(Clone)]
pub struct PersonController {
    id_counter: Arc<AtomicU32>,
    person_list: Arc<RwLock<Vec<Person>>>,
}

impl PersonController {
    pub fn new() -> Self {
        let person_list = vec![
            Person {
                id: 1,
                first_name: "Steven".to_string(),
                last_name: "Spielberg".to_string(),
                city_id: 1,
            },
            Person {
                id: 2,
                first_name: "Martin".to_string(),
                last_name: "Scorsese".to_string(),
                city_id: 2,
            },
            Person {
                id: 3,
                first_name: "Francis".to_string(),
                last_name: "Ford Coppola".to_string(),
                city_id: 3,
            },
            Person {
                id: 4,
                first_name: "George".to_string(),
                last_name: "Lucas".to_string(),
                city_id: 4,
            },
            Person {
                id: 5,
                first_name: "Quentin".to_string(),
                last_name: "Tarantino".to_string(),
                city_id: 5,
            },
            Person {
                id: 6,
                first_name: "David".to_string(),
                last_name: "Fincher".to_string(),
                city_id: 6,
            },
            Person {
                id: 7,
                first_name: "Spike".to_string(),
                last_name: "Lee".to_string(),
                city_id: 7,
            },
        ];

        Self {
            id_counter: Arc::new(
                AtomicU32::new(7),
            ),
            person_list: Arc::new(
                RwLock::new(person_list),
            ),
        }
    }
}

pub fn routes() -> Router {
    Router::new()
        .route(
            "/persons",
            get(get_all_persons)
                .post(create_person),
        )
        .route(
            "/persons/{id}",
            get(get_person_by_id)
                .put(update_person)
                .delete(delete_person),
        )
        .with_state(PersonController::new())
}

async fn get_all_persons(
    State(controller): State<PersonController>,
) -> Json<Vec<PersonOutputDto>> {
    let person_list =
        controller.person_list.read().await;

    let output = person_list
        .iter()
        .map(PersonOutputDto::from)
        .collect();

    Json(output)
}

async fn get_person_by_id(
    State(controller): State<PersonController>,
    Path(id): Path<u32>,
) -> Result<
    Json<PersonOutputDto>,
    StatusCode,
> {
    let person_list =
        controller.person_list.read().await;

    person_list
        .iter()
        .find(|person| person.id == id)
        .map(PersonOutputDto::from)
        .map(Json)
        .ok_or(StatusCode::NOT_FOUND)
}

async fn create_person(
    State(controller): State<PersonController>,
    Json(input): Json<PersonInputDto>,
) -> Result<
    (
        StatusCode,
        Json<PersonOutputDto>,
    ),
    StatusCode,
> {
    let input = input
        .validate_and_normalize()
        .map_err(
            |_| StatusCode::BAD_REQUEST,
        )?;

    let id = controller
        .id_counter
        .fetch_add(
            1,
            Ordering::SeqCst,
        )
        + 1;

    let person = Person {
        id,
        first_name: input.first_name,
        last_name: input.last_name,
        city_id: input.city_id,
    };

    controller
        .person_list
        .write()
        .await
        .push(person.clone());

    Ok((
        StatusCode::CREATED,
        Json(
            PersonOutputDto::from(
                &person,
            ),
        ),
    ))
}

async fn update_person(
    State(controller): State<PersonController>,
    Path(id): Path<u32>,
    Json(input): Json<PersonInputDto>,
) -> Result<Json<PersonOutputDto>, StatusCode> {
    let input = input
        .validate_and_normalize()
        .map_err(|_| StatusCode::BAD_REQUEST)?;

    let mut person_list =
        controller.person_list.write().await;

    let person = person_list
        .iter_mut()
        .find(|person| person.id == id)
        .ok_or(StatusCode::NOT_FOUND)?;

    person.first_name = input.first_name;
    person.last_name = input.last_name;
    person.city_id = input.city_id;

    Ok(Json(
        PersonOutputDto::from(&*person),
    ))
}

async fn delete_person(
    State(controller): State<PersonController>,
    Path(id): Path<u32>,
) -> Result<
    StatusCode,
    StatusCode,
> {
    let mut person_list =
        controller.person_list.write().await;

    let index = person_list
        .iter()
        .position(
            |person| person.id == id,
        )
        .ok_or(StatusCode::NOT_FOUND)?;

    person_list.remove(index);

    Ok(StatusCode::NO_CONTENT)
}