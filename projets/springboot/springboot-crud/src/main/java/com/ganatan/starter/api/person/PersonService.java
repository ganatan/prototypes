package com.ganatan.starter.api.person;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PersonService {

  private final PersonRepository personRepository;

  public PersonService(
      PersonRepository personRepository
  ) {
    this.personRepository =
        personRepository;
  }

  @Transactional(readOnly = true)
  public List<Person> getAllPersons() {
    return personRepository
        .findAllByOrderByIdAsc();
  }

  @Transactional(readOnly = true)
  public Optional<Person> getPersonById(
      int id
  ) {
    return personRepository
        .findById(id);
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

    return personRepository.save(
        person
    );
  }

  @Transactional
  public Optional<Person> updatePerson(
      int id,
      String firstName,
      String lastName,
      int cityId
  ) {
    Optional<Person> existing =
        personRepository.findById(id);

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
        personRepository.save(
            person
        )
    );
  }

  @Transactional
  public boolean deletePerson(
      int id
  ) {
    Optional<Person> existing =
        personRepository.findById(id);

    if (existing.isEmpty()) {
      return false;
    }

    personRepository.delete(
        existing.get()
    );

    return true;
  }
}