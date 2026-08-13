use sqlx::PgPool;

use super::person::Person;

#[derive(sqlx::FromRow)]
struct PersonRow {
    id: i32,
    first_name: String,
    last_name: String,
    city_id: i32,
}

impl From<PersonRow> for Person {
    fn from(row: PersonRow) -> Self {
        Self {
            id: row.id,
            first_name: row.first_name,
            last_name: row.last_name,
            city_id: row.city_id,
        }
    }
}

#[derive(Clone)]
pub struct PersonRepository {
    pool: PgPool,
}

impl PersonRepository {
    pub fn new(pool: PgPool) -> Self {
        Self { pool }
    }

    pub async fn find_all(&self) -> Result<Vec<Person>, sqlx::Error> {
        let rows = sqlx::query_as::<_, PersonRow>(
            r#"
                SELECT
                    id,
                    first_name,
                    last_name,
                    city_id
                FROM person
                ORDER BY id
                "#,
        )
        .fetch_all(&self.pool)
        .await?;
        Ok(rows.into_iter().map(Person::from).collect())
    }

    pub async fn find_by_id(&self, id: i32) -> Result<Option<Person>, sqlx::Error> {
        let row = sqlx::query_as::<_, PersonRow>(
            r#"
                SELECT
                    id,
                    first_name,
                    last_name,
                    city_id
                FROM person
                WHERE id = $1
                "#,
        )
        .bind(id)
        .fetch_optional(&self.pool)
        .await?;

        Ok(row.map(Person::from))
    }

    pub async fn create(
        &self,
        first_name: String,
        last_name: String,
        city_id: i32,
    ) -> Result<Person, sqlx::Error> {
        let row = sqlx::query_as::<_, PersonRow>(
            r#"
                INSERT INTO person (
                    first_name,
                    last_name,
                    city_id
                )
                VALUES (
                    $1,
                    $2,
                    $3
                )
                RETURNING
                    id,
                    first_name,
                    last_name,
                    city_id
                "#,
        )
        .bind(first_name)
        .bind(last_name)
        .bind(city_id)
        .fetch_one(&self.pool)
        .await?;

        Ok(Person::from(row))
    }

    pub async fn update(
        &self,
        id: i32,
        first_name: String,
        last_name: String,
        city_id: i32,
    ) -> Result<Option<Person>, sqlx::Error> {
        let row = sqlx::query_as::<_, PersonRow>(
            r#"
                UPDATE person
                SET
                    first_name = $1,
                    last_name = $2,
                    city_id = $3
                WHERE id = $4
                RETURNING
                    id,
                    first_name,
                    last_name,
                    city_id
                "#,
        )
        .bind(first_name)
        .bind(last_name)
        .bind(city_id)
        .bind(id)
        .fetch_optional(&self.pool)
        .await?;

        Ok(row.map(Person::from))
    }

    pub async fn delete(&self, id: i32) -> Result<bool, sqlx::Error> {
        let result = sqlx::query(
            r#"
                DELETE FROM person
                WHERE id = $1
                "#,
        )
        .bind(id)
        .execute(&self.pool)
        .await?;

        Ok(result.rows_affected() > 0)
    }
}
