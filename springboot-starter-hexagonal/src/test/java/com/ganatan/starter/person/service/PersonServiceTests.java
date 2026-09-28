package com.ganatan.starter.person.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ganatan.starter.person.application.exception.PersonNotFoundException;
import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.domain.Person;
import com.ganatan.starter.person.infrastructure.adapter.out.memory.InMemoryPersonRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PersonServiceTests {

  private PersonService service;

  @BeforeEach
  void setUp() {
    PersonRepository repository =
        new InMemoryPersonRepository();

    service =
        new PersonService(
            repository
        );
  }

  @Test
  void getAllShouldReturnSevenPersons() {
    List<Person> persons =
        service.getAll();

    assertNotNull(
        persons
    );

    assertEquals(
        7,
        persons.size()
    );
  }

  @Test
  void getByIdShouldReturnPersonWhenIdExists() {
    Person person =
        service.getById(
            1
        );

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
  }

  @Test
  void getByIdShouldThrowWhenIdDoesNotExist() {
    PersonNotFoundException exception =
        assertThrows(
            PersonNotFoundException.class,
            () ->
                service.getById(
                    999
                )
        );

    assertEquals(
        "Person not found: 999",
        exception.getMessage()
    );
  }

  @Test
  void createShouldReturnGeneratedId() {
    Person input =
        new Person(
            0,
            "Clint",
            "Eastwood",
            8
        );

    Person result =
        service.create(
            input
        );

    assertEquals(
        8,
        result.id()
    );

    assertEquals(
        "Clint",
        result.firstName()
    );

    assertEquals(
        "Eastwood",
        result.lastName()
    );

    assertEquals(
        8,
        result.cityId()
    );
  }

  @Test
  void createShouldIgnoreInputId() {
    Person input =
        new Person(
            999,
            "Clint",
            "Eastwood",
            8
        );

    Person result =
        service.create(
            input
        );

    assertNotEquals(
        999,
        result.id()
    );

    assertEquals(
        8,
        result.id()
    );
  }

  @Test
  void createShouldIncreaseSizeByOne() {
    int before =
        service.getAll()
            .size();

    service.create(
        new Person(
            0,
            "Clint",
            "Eastwood",
            8
        )
    );

    assertEquals(
        before + 1,
        service.getAll()
            .size()
    );
  }

  @Test
  void updateShouldModifyPerson() {
    Person input =
        new Person(
            0,
            "Steven",
            "Spielberg",
            10
        );

    Person result =
        service.update(
            1,
            input
        );

    assertEquals(
        1,
        result.id()
    );

    assertEquals(
        "Steven",
        result.firstName()
    );

    assertEquals(
        "Spielberg",
        result.lastName()
    );

    assertEquals(
        10,
        result.cityId()
    );
  }

  @Test
  void updateShouldPreserveIdFromParameter() {
    Person input =
        new Person(
            999,
            "Steven",
            "Spielberg",
            10
        );

    Person result =
        service.update(
            1,
            input
        );

    assertEquals(
        1,
        result.id()
    );
  }

  @Test
  void updateShouldReflectChange() {
    service.update(
        1,
        new Person(
            0,
            "Steven",
            "Spielberg",
            10
        )
    );

    Person result =
        service.getById(
            1
        );

    assertEquals(
        10,
        result.cityId()
    );
  }

  @Test
  void updateShouldThrowWhenIdDoesNotExist() {
    assertThrows(
        PersonNotFoundException.class,
        () ->
            service.update(
                999,
                new Person(
                    0,
                    "Unknown",
                    "Person",
                    1
                )
            )
    );
  }

  @Test
  void deleteShouldRemovePerson() {
    int before =
        service.getAll()
            .size();

    service.delete(
        1
    );

    assertEquals(
        before - 1,
        service.getAll()
            .size()
    );
  }

  @Test
  void deleteShouldRemoveCorrectPerson() {
    service.delete(
        1
    );

    boolean exists =
        service.getAll()
            .stream()
            .anyMatch(
                person ->
                    person.id() == 1
            );

    assertFalse(
        exists
    );
  }

  @Test
  void deleteShouldMakePersonUnavailable() {
    service.delete(
        1
    );

    assertThrows(
        PersonNotFoundException.class,
        () ->
            service.getById(
                1
            )
    );
  }

  @Test
  void deleteShouldThrowWhenIdDoesNotExist() {
    assertThrows(
        PersonNotFoundException.class,
        () ->
            service.delete(
                999
            )
    );
  }

}