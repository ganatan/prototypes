import asyncpg

from .person import Person


class PersonRepository:
    def __init__(self, pool: asyncpg.Pool):
        self.pool = pool

    @staticmethod
    def _to_person(row: asyncpg.Record) -> Person:
        return Person(
            id=row["id"],
            first_name=row["first_name"],
            last_name=row["last_name"],
            city_id=row["city_id"],
        )

    async def find_all(self) -> list[Person]:
        rows = await self.pool.fetch(
            '''
            SELECT
                id,
                first_name,
                last_name,
                city_id
            FROM person
            ORDER BY id
            '''
        )

        return [self._to_person(row) for row in rows]

    async def find_by_id(self, id: int) -> Person | None:
        row = await self.pool.fetchrow(
            '''
            SELECT
                id,
                first_name,
                last_name,
                city_id
            FROM person
            WHERE id = $1
            ''',
            id,
        )

        if row is None:
            return None

        return self._to_person(row)

    async def create(
        self,
        first_name: str,
        last_name: str,
        city_id: int,
    ) -> Person:
        row = await self.pool.fetchrow(
            '''
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
            ''',
            first_name,
            last_name,
            city_id,
        )

        return self._to_person(row)

    async def update(
        self,
        id: int,
        first_name: str,
        last_name: str,
        city_id: int,
    ) -> Person | None:
        row = await self.pool.fetchrow(
            '''
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
            ''',
            first_name,
            last_name,
            city_id,
            id,
        )

        if row is None:
            return None

        return self._to_person(row)

    async def delete(self, id: int) -> bool:
        result = await self.pool.execute(
            '''
            DELETE FROM person
            WHERE id = $1
            ''',
            id,
        )

        return result == "DELETE 1"
