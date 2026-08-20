package com.ganatan.starter.api.person;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonService {

  private final PersonRepository personRepository;

  public PersonService(
      PersonRepository personRepository
  ) {

    this.personRepository =
        personRepository;

  }

  public List<Person> getAllPersons() {

    return personRepository
        .findAll();

  }

  public Optional<Person> getPersonById(
      int id
  ) {

    return personRepository
        .findById(id);

  }

  public Person createPerson(
      String firstName,
      String lastName,
      int cityId
  ) {

    return personRepository
        .create(
            firstName,
            lastName,
            cityId
        );

  }

  public Optional<Person> updatePerson(
      int id,
      String firstName,
      String lastName,
      int cityId
  ) {

    return personRepository
        .update(
            id,
            firstName,
            lastName,
            cityId
        );

  }

  public boolean deletePerson(
      int id
  ) {

    return personRepository
        .delete(id);

  }

}