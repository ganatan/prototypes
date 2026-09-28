package com.ganatan.starter.api.person;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PersonInputDto(

    @NotBlank
    @Size(max = 50)
    String firstName,

    @NotBlank
    @Size(max = 50)
    String lastName,

    @NotNull
    @Positive
    Integer cityId

) {

  public PersonInputDto {
    firstName = firstName == null
        ? null
        : firstName.trim();

    lastName = lastName == null
        ? null
        : lastName.trim();
  }
}