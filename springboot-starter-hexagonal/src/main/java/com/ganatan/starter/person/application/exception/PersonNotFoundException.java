package com.ganatan.starter.person.application.exception;

public class PersonNotFoundException extends RuntimeException {

  public PersonNotFoundException(
      int id
  ) {
    super(
        "Person not found: " + id
    );
  }

}