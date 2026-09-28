package com.ganatan.starter.person.infrastructure.adapter.out.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.ganatan.starter.person.domain.Person;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryPersonRepositoryTests {

  private InMemoryPersonRepository repository;

  @BeforeEach
  void setUp() {
    repository =
        new InMemoryPersonRepository();
  }

  @Test
  void findAllShouldReturnSevenPersons() {
    List<Person> persons =
        repository.findAll();

    assertNotNull(
        persons
    );

    assertEquals(
        7,
        persons.size()
    );
  }

  @Test
  void findAllShouldContainStevenSpielberg() {
    List<Person> persons =
        repository.findAll();

    assertTrue(
        persons.stream()
            .anyMatch(
                person ->
                    person.firstName()
                        .equals("Steven")
                        && person.lastName()
                        .equals("Spielberg")
            )
    );
  }

  @Test
  void findAllShouldHaveSequentialIds() {
    List<Person> persons =
        repository.findAll();

    for (
        int index = 0;
        index < persons.size();
        index++
    ) {
      assertEquals(
          index + 1,
          persons.get(index).id()
      );
    }
  }

  @Test
  void findByIdShouldReturnPersonWhenIdExists() {
    Optional<Person> result =
        repository.findById(
            1
        );

    assertTrue(
        result.isPresent()
    );

    Person person =
        result.orElseThrow();

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
  void findByIdShouldReturnEmptyWhenIdDoesNotExist() {
    Optional<Person> result =
        repository.findById(
            999
        );

    assertTrue(
        result.isEmpty()
    );
  }

  @Test
  void createShouldAddPersonAndGenerateId() {
    Person input =
        new Person(
            0,
            "Clint",
            "Eastwood",
            8
        );

    Person result =
        repository.create(
            input
        );

    assertNotNull(
        result
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
  void createShouldIncreaseSizeByOne() {
    int before =
        repository.findAll()
            .size();

    repository.create(
        new Person(
            0,
            "Clint",
            "Eastwood",
            8
        )
    );

    assertEquals(
        before + 1,
        repository.findAll()
            .size()
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
        repository.create(
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
  void updateShouldModifyPersonWhenIdExists() {
    Person modified =
        new Person(
            1,
            "Steven",
            "Spielberg",
            10
        );

    Person result =
        repository.update(
            modified
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
  void updateShouldReflectChangeInRepository() {
    repository.update(
        new Person(
            1,
            "Steven",
            "Spielberg",
            10
        )
    );

    Person person =
        repository.findById(
                1
            )
            .orElseThrow();

    assertEquals(
        10,
        person.cityId()
    );
  }

  @Test
  void updateShouldThrowWhenIdDoesNotExist() {
    assertThrows(
        NoSuchElementException.class,
        () ->
            repository.update(
                new Person(
                    999,
                    "Unknown",
                    "Person",
                    1
                )
            )
    );
  }

  @Test
  void deleteByIdShouldRemovePerson() {
    int before =
        repository.findAll()
            .size();

    repository.deleteById(
        1
    );

    assertEquals(
        before - 1,
        repository.findAll()
            .size()
    );
  }

  @Test
  void deleteByIdShouldRemoveCorrectPerson() {
    repository.deleteById(
        1
    );

    boolean exists =
        repository.findAll()
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
  void deleteByIdShouldMakeIdUnavailable() {
    repository.deleteById(
        1
    );

    Optional<Person> result =
        repository.findById(
            1
        );

    assertTrue(
        result.isEmpty()
    );
  }

  @Test
  void deleteByIdShouldThrowWhenIdDoesNotExist() {
    assertThrows(
        NoSuchElementException.class,
        () ->
            repository.deleteById(
                999
            )
    );
  }

}