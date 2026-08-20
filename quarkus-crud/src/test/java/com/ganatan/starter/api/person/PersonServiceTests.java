package com.ganatan.starter.api.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        repository.findAll()
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
    ).findAll();

  }

  @Test
  void getPersonById_shouldReturnPerson() {

    when(
        repository.findById(1)
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
        person.id()
    );

    assertEquals(
        "Steven",
        person.firstName()
    );

    assertEquals(
        "Spielberg",
        person.lastName()
    );

    assertEquals(
        1,
        person.cityId()
    );

    verify(
        repository
    ).findById(1);

  }

  @Test
  void getPersonById_shouldReturnEmpty() {

    when(
        repository.findById(999)
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
    ).findById(999);

  }

  @Test
  void createPerson_shouldReturnCreatedPerson() {

    when(
        repository.create(
            "Clint",
            "Eastwood",
            8
        )
    ).thenReturn(
        new Person(
            8,
            "Clint",
            "Eastwood",
            8
        )
    );

    Person person =
        service.createPerson(
            "Clint",
            "Eastwood",
            8
        );

    assertEquals(
        8,
        person.id()
    );

    assertEquals(
        "Clint",
        person.firstName()
    );

    assertEquals(
        "Eastwood",
        person.lastName()
    );

    assertEquals(
        8,
        person.cityId()
    );

    verify(
        repository
    ).create(
        "Clint",
        "Eastwood",
        8
    );

  }

  @Test
  void updatePerson_shouldReturnUpdatedPerson() {

    when(
        repository.update(
            1,
            "Steven",
            "Spielberg Updated",
            10
        )
    ).thenReturn(
        Optional.of(
            new Person(
                1,
                "Steven",
                "Spielberg Updated",
                10
            )
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
        person.id()
    );

    assertEquals(
        "Spielberg Updated",
        person.lastName()
    );

    assertEquals(
        10,
        person.cityId()
    );

    verify(
        repository
    ).update(
        1,
        "Steven",
        "Spielberg Updated",
        10
    );

  }

  @Test
  void updatePerson_shouldReturnEmpty() {

    when(
        repository.update(
            999,
            "Unknown",
            "Person",
            1
        )
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

    verify(
        repository
    ).update(
        999,
        "Unknown",
        "Person",
        1
    );

  }

  @Test
  void deletePerson_shouldReturnTrue() {

    when(
        repository.delete(1)
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
    ).delete(1);

  }

  @Test
  void deletePerson_shouldReturnFalse() {

    when(
        repository.delete(999)
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
    ).delete(999);

  }

}