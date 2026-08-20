from .person import Person
from .repository import PersonRepository


class PersonService:
    def __init__(self, person_repository: PersonRepository):
        self.person_repository = person_repository

    async def get_all(self) -> list[Person]:
        return await self.person_repository.find_all()

    async def get_by_id(self, id: int) -> Person | None:
        return await self.person_repository.find_by_id(id)

    async def create(
        self,
        first_name: str,
        last_name: str,
        city_id: int,
    ) -> Person:
        return await self.person_repository.create(
            first_name,
            last_name,
            city_id,
        )

    async def update(
        self,
        id: int,
        first_name: str,
        last_name: str,
        city_id: int,
    ) -> Person | None:
        return await self.person_repository.update(
            id,
            first_name,
            last_name,
            city_id,
        )

    async def delete(self, id: int) -> bool:
        return await self.person_repository.delete(id)
