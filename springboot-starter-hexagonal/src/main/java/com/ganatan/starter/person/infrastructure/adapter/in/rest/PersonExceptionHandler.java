package com.ganatan.starter.person.infrastructure.adapter.in.rest;

import com.ganatan.starter.person.application.exception.PersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
    assignableTypes = PersonController.class
)
public class PersonExceptionHandler {

  @ExceptionHandler(
      PersonNotFoundException.class
  )
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handlePersonNotFound() {
  }

}