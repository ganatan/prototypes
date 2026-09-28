package com.ganatan.starter.api.person;

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
        person.getId(),
        person.getFirstName(),
        person.getLastName(),
        person.getCityId()
    );

  }

}