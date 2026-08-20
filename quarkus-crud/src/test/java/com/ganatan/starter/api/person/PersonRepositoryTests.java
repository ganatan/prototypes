package com.ganatan.starter.api.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;

import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

@QuarkusTest
@TestTransaction
class PersonRepositoryTests {

  @Inject
  PersonRepository repository;

  @Test
  void findAll_shouldReturnSevenPersons() {

    List<Person> persons =
        repository
            .findAllByOrderByIdAsc();

    assertEquals(
        7,
        persons.size()
    );

  }

  @Test
  void findById_shouldReturnPerson() {

    Person person = repository
        .findByIdOptional(1)
        .orElseThrow();

    assertEquals(
        1,
        person.getId()
    );

    assertEquals(
        "Steven",
        person.getFirstName()
    );

    assertEquals(
        "Spielberg",
        person.getLastName()
    );

    assertEquals(
        1,
        person.getCityId()
    );

  }

  @Test
  void findById_shouldReturnEmpty() {

    Optional<Person> person =
        repository
            .findByIdOptional(999);

    assertTrue(
        person.isEmpty()
    );

  }

  @Test
  void persist_shouldCreatePerson() {

    Person person =
        new Person(
            "Clint",
            "Eastwood",
            8
        );

    repository.persist(
        person
    );

    repository.flush();

    assertNotNull(
        person.getId()
    );

    assertEquals(
        "Clint",
        person.getFirstName()
    );

    assertEquals(
        "Eastwood",
        person.getLastName()
    );

    assertEquals(
        8,
        person.getCityId()
    );

  }

  @Test
  void update_shouldModifyPerson() {

    Person person =
        new Person(
            "Clint",
            "Eastwood",
            8
        );

    repository.persist(
        person
    );

    repository.flush();

    Integer id =
        person.getId();

    person.setLastName(
        "Eastwood Updated"
    );

    person.setCityId(
        10
    );

    repository.flush();

    Person updated = repository
        .findByIdOptional(id)
        .orElseThrow();

    assertEquals(
        "Clint",
        updated.getFirstName()
    );

    assertEquals(
        "Eastwood Updated",
        updated.getLastName()
    );

    assertEquals(
        10,
        updated.getCityId()
    );

  }

  @Test
  void delete_shouldRemovePerson() {

    Person person =
        new Person(
            "Clint",
            "Eastwood",
            8
        );

    repository.persist(
        person
    );

    repository.flush();

    Integer id =
        person.getId();

    repository.delete(
        person
    );

    repository.flush();

    Optional<Person> result =
        repository
            .findByIdOptional(id);

    assertTrue(
        result.isEmpty()
    );

  }

}