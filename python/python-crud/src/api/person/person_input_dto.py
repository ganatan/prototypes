from pydantic import BaseModel, ConfigDict, Field, StrictInt, StrictStr


class PersonInputDto(BaseModel):
    model_config = ConfigDict(populate_by_name=True)

    first_name: StrictStr = Field(alias="firstName")
    last_name: StrictStr = Field(alias="lastName")
    city_id: StrictInt = Field(alias="cityId")

    def validate_and_normalize(self) -> "PersonInputDto":
        first_name = self.first_name.strip()
        last_name = self.last_name.strip()

        if (
            not first_name
            or len(first_name) > 50
            or not last_name
            or len(last_name) > 50
            or self.city_id <= 0
        ):
            raise ValueError

        return PersonInputDto(
            first_name=first_name,
            last_name=last_name,
            city_id=self.city_id,
        )
