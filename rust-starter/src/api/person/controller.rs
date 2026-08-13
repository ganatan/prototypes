use axum::{
    Json, Router,
    extract::{Path, State},
    http::StatusCode,
    routing::get,
};
use sqlx::PgPool;

use super::{
    person_input_dto::PersonInputDto, person_output_dto::PersonOutputDto,
    repository::PersonRepository, service::PersonService,
};

#[derive(Clone)]
pub struct PersonController {
    person_service: PersonService,
}

impl PersonController {
    pub fn new(person_service: PersonService) -> Self {
        Self { person_service }
    }
}

pub fn routes(pool: PgPool) -> Router {
    let repository = PersonRepository::new(pool);

    let service = PersonService::new(repository);

    let controller = PersonController::new(service);

    Router::new()
        .route("/persons", get(get_all_persons).post(create_person))
        .route(
            "/persons/{id}",
            get(get_person_by_id)
                .put(update_person)
                .delete(delete_person),
        )
        .with_state(controller)
}

async fn get_all_persons(
    State(controller): State<PersonController>,
) -> Result<Json<Vec<PersonOutputDto>>, StatusCode> {
    let persons = controller
        .person_service
        .get_all()
        .await
        .map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)?;

    let output = persons.iter().map(PersonOutputDto::from).collect();

    Ok(Json(output))
}

async fn get_person_by_id(
    State(controller): State<PersonController>,
    Path(id): Path<i32>,
) -> Result<Json<PersonOutputDto>, StatusCode> {
    let person = controller
        .person_service
        .get_by_id(id)
        .await
        .map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)?
        .ok_or(StatusCode::NOT_FOUND)?;

    Ok(Json(PersonOutputDto::from(&person)))
}

async fn create_person(
    State(controller): State<PersonController>,
    Json(input): Json<PersonInputDto>,
) -> Result<(StatusCode, Json<PersonOutputDto>), StatusCode> {
    let input = input
        .validate_and_normalize()
        .map_err(|_| StatusCode::BAD_REQUEST)?;

    let person = controller
        .person_service
        .create(input.first_name, input.last_name, input.city_id)
        .await
        .map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)?;

    Ok((StatusCode::CREATED, Json(PersonOutputDto::from(&person))))
}

async fn update_person(
    State(controller): State<PersonController>,
    Path(id): Path<i32>,
    Json(input): Json<PersonInputDto>,
) -> Result<Json<PersonOutputDto>, StatusCode> {
    let input = input
        .validate_and_normalize()
        .map_err(|_| StatusCode::BAD_REQUEST)?;

    let person = controller
        .person_service
        .update(id, input.first_name, input.last_name, input.city_id)
        .await
        .map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)?
        .ok_or(StatusCode::NOT_FOUND)?;

    Ok(Json(PersonOutputDto::from(&person)))
}

async fn delete_person(
    State(controller): State<PersonController>,
    Path(id): Path<i32>,
) -> Result<StatusCode, StatusCode> {
    let deleted = controller
        .person_service
        .delete(id)
        .await
        .map_err(|_| StatusCode::INTERNAL_SERVER_ERROR)?;

    if !deleted {
        return Err(StatusCode::NOT_FOUND);
    }

    Ok(StatusCode::NO_CONTENT)
}
