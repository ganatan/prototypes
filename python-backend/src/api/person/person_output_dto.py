from pydantic import BaseModel, ConfigDict, Field

from .person import Person


class PersonOutputDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    id: int
    first_name: str = Field(alias="firstName")
    last_name: str = Field(alias="lastName")
    city_id: int = Field(alias="cityId")

    @classmethod
    def from_person(cls, person: Person) -> "PersonOutputDto":
        return cls(
            id=person.id,
            first_name=person.first_name,
            last_name=person.last_name,
            city_id=person.city_id,
        )
