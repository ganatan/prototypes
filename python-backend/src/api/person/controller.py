import asyncpg
from fastapi import APIRouter, HTTPException, Response, status

from .person_input_dto import PersonInputDto
from .person_output_dto import PersonOutputDto
from .repository import PersonRepository
from .service import PersonService


class PersonController:
    def __init__(self, person_service: PersonService):
        self.person_service = person_service


def routes(pool: asyncpg.Pool) -> APIRouter:
    repository = PersonRepository(pool)
    service = PersonService(repository)
    controller = PersonController(service)

    router = APIRouter()

    @router.get(
        "/persons",
        response_model=list[PersonOutputDto],
    )
    async def get_all_persons() -> list[PersonOutputDto]:
        try:
            persons = await controller.person_service.get_all()
        except asyncpg.PostgresError as error:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
            ) from error

        return [
            PersonOutputDto.from_person(person)
            for person in persons
        ]

    @router.get(
        "/persons/{id}",
        response_model=PersonOutputDto,
    )
    async def get_person_by_id(id: int) -> PersonOutputDto:
        try:
            person = await controller.person_service.get_by_id(id)
        except asyncpg.PostgresError as error:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
            ) from error

        if person is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND
            )

        return PersonOutputDto.from_person(person)

    @router.post(
        "/persons",
        response_model=PersonOutputDto,
        status_code=status.HTTP_201_CREATED,
    )
    async def create_person(
        input: PersonInputDto,
    ) -> PersonOutputDto:
        try:
            input = input.validate_and_normalize()
        except ValueError as error:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST
            ) from error

        try:
            person = await controller.person_service.create(
                input.first_name,
                input.last_name,
                input.city_id,
            )
        except asyncpg.PostgresError as error:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
            ) from error

        return PersonOutputDto.from_person(person)

    @router.put(
        "/persons/{id}",
        response_model=PersonOutputDto,
    )
    async def update_person(
        id: int,
        input: PersonInputDto,
    ) -> PersonOutputDto:
        try:
            input = input.validate_and_normalize()
        except ValueError as error:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST
            ) from error

        try:
            person = await controller.person_service.update(
                id,
                input.first_name,
                input.last_name,
                input.city_id,
            )
        except asyncpg.PostgresError as error:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
            ) from error

        if person is None:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND
            )

        return PersonOutputDto.from_person(person)

    @router.delete(
        "/persons/{id}",
        status_code=status.HTTP_204_NO_CONTENT,
    )
    async def delete_person(id: int) -> Response:
        try:
            deleted = await controller.person_service.delete(id)
        except asyncpg.PostgresError as error:
            raise HTTPException(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
            ) from error

        if not deleted:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND
            )

        return Response(
            status_code=status.HTTP_204_NO_CONTENT
        )

    return router
