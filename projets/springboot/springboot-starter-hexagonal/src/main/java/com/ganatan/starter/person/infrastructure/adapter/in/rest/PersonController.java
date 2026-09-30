package com.ganatan.starter.person.infrastructure.adapter.in.rest;

import com.ganatan.starter.person.application.port.in.PersonUseCase;
import com.ganatan.starter.person.domain.Person;
import com.ganatan.starter.person.infrastructure.adapter.in.rest.dto.PersonInputDto;
import com.ganatan.starter.person.infrastructure.adapter.in.rest.dto.PersonOutputDto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
j'ai fait ma revision'
@RestController
@RequestMapping("/persons")
public class PersonController {

  private final PersonUseCase personUseCase;

  public PersonController(
      PersonUseCase personUseCase
  ) {
    this.personUseCase = personUseCase;
  }

  @GetMapping
  public List<PersonOutputDto> getAll() {
    return personUseCase.getAll()
        .stream()
        .map(
            PersonOutputDto::from
        )
        .toList();
  }

  @GetMapping("/{id}")
  public PersonOutputDto getById(
      @PathVariable int id
  ) {
    Person person =
        personUseCase.getById(
            id
        );

    return PersonOutputDto.from(
        person
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PersonOutputDto create(
      @RequestBody PersonInputDto input
  ) {
    Person person =
        personUseCase.create(
            input.toDomain()
        );

    return PersonOutputDto.from(
        person
    );
  }

  @PutMapping("/{id}")
  public PersonOutputDto update(
      @PathVariable int id,
      @RequestBody PersonInputDto input
  ) {
    Person person =
        personUseCase.update(
            id,
            input.toDomain()
        );

    return PersonOutputDto.from(
        person
    );
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable int id
  ) {
    personUseCase.delete(
        id
    );
  }

}