package com.ganatan.starter.person.infrastructure.adapter.in.rest.dto;

import com.ganatan.starter.person.domain.Person;

public record PersonOutputDto(
    int id,
    String firstName,
    String lastName,
    int cityId
) {

  public static PersonOutputDto from(
      Person person
  ) {
    return new PersonOutputDto(
        person.id(),
        person.firstName(),
        person.lastName(),
        person.cityId()
    );
  }

}