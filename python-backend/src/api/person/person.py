from dataclasses import dataclass


@dataclass(frozen=True, slots=True)
class Person:
    id: int
    first_name: str
    last_name: str
    city_id: int
