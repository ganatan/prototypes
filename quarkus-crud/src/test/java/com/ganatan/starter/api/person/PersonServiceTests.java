package com.ganatan.starter.api.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

class PersonServiceTests {

  private PersonRepository repository;

  private PersonService service;

  @BeforeEach
  void setUp() {

    repository =
        Mockito.mock(
            PersonRepository.class
        );

    service =
        new PersonService(
            repository
        );

  }

  @Test
  void getAllPersons_shouldReturnPersons() {

    when(
        repository
            .findAllByOrderByIdAsc()
    ).thenReturn(
        List.of(
            new Person(
                1,
                "Steven",
                "Spielberg",
                1
            ),
            new Person(
                2,
                "Martin",
                "Scorsese",
                2
            )
        )
    );

    List<Person> persons =
        service.getAllPersons();

    assertEquals(
        2,
        persons.size()
    );

    verify(
        repository
    ).findAllByOrderByIdAsc();

  }

  @Test
  void getPersonById_shouldReturnPerson() {

    when(
        repository
            .findByIdOptional(1)
    ).thenReturn(
        Optional.of(
            new Person(
                1,
                "Steven",
                "Spielberg",
                1
            )
        )
    );

    Person person = service
        .getPersonById(1)
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

    verify(
        repository
    ).findByIdOptional(1);

  }

  @Test
  void getPersonById_shouldReturnEmpty() {

    when(
        repository
            .findByIdOptional(999)
    ).thenReturn(
        Optional.empty()
    );

    Optional<Person> person =
        service.getPersonById(999);

    assertTrue(
        person.isEmpty()
    );

    verify(
        repository
    ).findByIdOptional(999);

  }

  @Test
  void createPerson_shouldPersistPerson() {

    Person person =
        service.createPerson(
            "Clint",
            "Eastwood",
            8
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

    verify(
        repository
    ).persist(
        any(Person.class)
    );

  }

  @Test
  void updatePerson_shouldModifyPerson() {

    Person existing =
        new Person(
            1,
            "Steven",
            "Spielberg",
            1
        );

    when(
        repository
            .findByIdOptional(1)
    ).thenReturn(
        Optional.of(
            existing
        )
    );

    Person person = service
        .updatePerson(
            1,
            "Steven",
            "Spielberg Updated",
            10
        )
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
        "Spielberg Updated",
        person.getLastName()
    );

    assertEquals(
        10,
        person.getCityId()
    );

    verify(
        repository
    ).findByIdOptional(1);

  }

  @Test
  void updatePerson_shouldReturnEmpty() {

    when(
        repository
            .findByIdOptional(999)
    ).thenReturn(
        Optional.empty()
    );

    Optional<Person> person =
        service.updatePerson(
            999,
            "Unknown",
            "Person",
            1
        );

    assertTrue(
        person.isEmpty()
    );

  }

  @Test
  void deletePerson_shouldReturnTrue() {

    when(
        repository.deleteById(1)
    ).thenReturn(
        true
    );

    boolean deleted =
        service.deletePerson(1);

    assertTrue(
        deleted
    );

    verify(
        repository
    ).deleteById(1);

  }

  @Test
  void deletePerson_shouldReturnFalse() {

    when(
        repository.deleteById(999)
    ).thenReturn(
        false
    );

    boolean deleted =
        service.deletePerson(999);

    assertFalse(
        deleted
    );

    verify(
        repository
    ).deleteById(999);

  }

}