package com.ganatan.starter.api.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    Person person1 =
        new Person(
            "Steven",
            "Spielberg",
            1
        );

    Person person2 =
        new Person(
            "Martin",
            "Scorsese",
            2
        );

    when(
        repository.findAllByOrderByIdAsc()
    ).thenReturn(
        List.of(
            person1,
            person2
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
    Person person =
        new Person(
            "Steven",
            "Spielberg",
            1
        );

    when(
        repository.findById(1)
    ).thenReturn(
        Optional.of(person)
    );

    Optional<Person> result =
        service.getPersonById(1);

    assertTrue(
        result.isPresent()
    );

    assertEquals(
        "Steven",
        result
            .orElseThrow()
            .getFirstName()
    );

    verify(
        repository
    ).findById(1);
  }

  @Test
  void createPerson_shouldSavePerson() {
    Person saved =
        new Person(
            "Clint",
            "Eastwood",
            8
        );

    when(
        repository.save(
            Mockito.any(Person.class)
        )
    ).thenReturn(saved);

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
    ).save(
        Mockito.any(Person.class)
    );
  }

  @Test
  void deletePerson_shouldReturnFalse() {
    when(
        repository.findById(999)
    ).thenReturn(
        Optional.empty()
    );

    boolean deleted =
        service.deletePerson(999);

    assertEquals(
        false,
        deleted
    );
  }
}