package com.ganatan.starter.person.infrastructure.adapter.in.rest.dto;

import com.ganatan.starter.person.domain.Person;

public record PersonInputDto(
    String firstName,
    String lastName,
    int cityId
) {

  public Person toDomain() {
    return new Person(
        0,
        firstName,
        lastName,
        cityId
    );
  }

}