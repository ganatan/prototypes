package com.ganatan.starter.api.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

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
        .findAllByOrderByIdAsc();

  }

  public Optional<Person> getPersonById(
      int id
  ) {

    return personRepository
        .findByIdOptional(id);

  }

  @Transactional
  public Person createPerson(
      String firstName,
      String lastName,
      int cityId
  ) {

    Person person = new Person(
        firstName,
        lastName,
        cityId
    );

    personRepository.persist(
        person
    );

    return person;

  }

  @Transactional
  public Optional<Person> updatePerson(
      int id,
      String firstName,
      String lastName,
      int cityId
  ) {

    Optional<Person> existing =
        personRepository
            .findByIdOptional(id);

    if (existing.isEmpty()) {

      return Optional.empty();

    }

    Person person =
        existing.get();

    person.setFirstName(
        firstName
    );

    person.setLastName(
        lastName
    );

    person.setCityId(
        cityId
    );

    return Optional.of(
        person
    );

  }

  @Transactional
  public boolean deletePerson(
      int id
  ) {

    return personRepository
        .deleteById(id);

  }

}