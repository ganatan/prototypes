package com.ganatan.starter.person.application.service;

import com.ganatan.starter.person.application.exception.PersonNotFoundException;
import com.ganatan.starter.person.application.port.in.PersonUseCase;
import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.domain.Person;
import java.util.List;

public class PersonService implements PersonUseCase {

  private final PersonRepository repository;

  public PersonService(
      PersonRepository repository
  ) {
    this.repository = repository;
  }

  @Override
  public List<Person> getAll() {
    return repository.findAll();
  }

  @Override
  public Person getById(
      int id
  ) {
    return repository.findById(
            id
        )
        .orElseThrow(
            () ->
                new PersonNotFoundException(
                    id
                )
        );
  }

  @Override
  public Person create(
      Person person
  ) {
    return repository.create(
        person
    );
  }

  @Override
  public Person update(
      int id,
      Person person
  ) {
    getById(
        id
    );

    Person updated =
        new Person(
            id,
            person.firstName(),
            person.lastName(),
            person.cityId()
        );

    return repository.update(
        updated
    );
  }

  @Override
  public void delete(
      int id
  ) {
    getById(
        id
    );

    repository.deleteById(
        id
    );
  }

}