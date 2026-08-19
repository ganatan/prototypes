package com.ganatan.starter.api.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

@DataJpaTest
@AutoConfigureTestDatabase(
    replace =
        AutoConfigureTestDatabase.Replace.NONE
)
class PersonRepositoryTests {

  @Autowired
  private PersonRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();

    repository.saveAll(
        List.of(
            new Person(
                "Steven",
                "Spielberg",
                1
            ),
            new Person(
                "Martin",
                "Scorsese",
                2
            ),
            new Person(
                "Francis",
                "Ford Coppola",
                3
            ),
            new Person(
                "George",
                "Lucas",
                4
            ),
            new Person(
                "Quentin",
                "Tarantino",
                5
            ),
            new Person(
                "David",
                "Fincher",
                6
            ),
            new Person(
                "Spike",
                "Lee",
                7
            )
        )
    );
  }

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
    Person saved =
        repository.save(
            new Person(
                "Clint",
                "Eastwood",
                8
            )
        );

    Optional<Person> person =
        repository.findById(
            saved.getId()
        );

    assertTrue(
        person.isPresent()
    );

    assertEquals(
        "Clint",
        person
            .orElseThrow()
            .getFirstName()
    );
  }

  @Test
  void save_shouldCreatePerson() {
    Person person =
        repository.save(
            new Person(
                "Clint",
                "Eastwood",
                8
            )
        );

    assertTrue(
        person.getId() > 0
    );

    assertEquals(
        "Clint",
        person.getFirstName()
    );

    assertEquals(
        "Eastwood",
        person.getLastName()
    );
  }

  @Test
  void delete_shouldRemovePerson() {
    Person person =
        repository.save(
            new Person(
                "Clint",
                "Eastwood",
                8
            )
        );

    Integer id =
        person.getId();

    repository.delete(
        person
    );

    Optional<Person> result =
        repository.findById(id);

    assertTrue(
        result.isEmpty()
    );
  }
}