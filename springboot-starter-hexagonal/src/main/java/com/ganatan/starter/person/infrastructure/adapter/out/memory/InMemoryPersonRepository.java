package com.ganatan.starter.person.infrastructure.adapter.out.memory;

import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.domain.Person;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryPersonRepository implements PersonRepository {

  private final AtomicInteger idCounter =
      new AtomicInteger(0);

  private final List<Person> personList =
      new ArrayList<>();

  public InMemoryPersonRepository() {
    createInitialPerson(
        "Steven",
        "Spielberg",
        1
    );

    createInitialPerson(
        "Martin",
        "Scorsese",
        2
    );

    createInitialPerson(
        "Francis",
        "Ford Coppola",
        3
    );

    createInitialPerson(
        "George",
        "Lucas",
        4
    );

    createInitialPerson(
        "Quentin",
        "Tarantino",
        5
    );

    createInitialPerson(
        "David",
        "Fincher",
        6
    );

    createInitialPerson(
        "Spike",
        "Lee",
        7
    );
  }

  @Override
  public List<Person> findAll() {
    return List.copyOf(
        personList
    );
  }

  @Override
  public Optional<Person> findById(
      int id
  ) {
    return personList.stream()
        .filter(
            person ->
                person.id() == id
        )
        .findFirst();
  }

  @Override
  public Person create(
      Person person
  ) {
    int id =
        idCounter.incrementAndGet();

    Person created =
        new Person(
            id,
            person.firstName(),
            person.lastName(),
            person.cityId()
        );

    personList.add(
        created
    );

    return created;
  }

  @Override
  public Person update(
      Person person
  ) {
    Person existing =
        findById(
            person.id()
        )
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Person not found: "
                            + person.id()
                    )
            );

    int index =
        personList.indexOf(
            existing
        );

    personList.set(
        index,
        person
    );

    return person;
  }

  @Override
  public void deleteById(
      int id
  ) {
    Person existing =
        findById(
            id
        )
            .orElseThrow(
                () ->
                    new NoSuchElementException(
                        "Person not found: "
                            + id
                    )
            );

    personList.remove(
        existing
    );
  }

  private void createInitialPerson(
      String firstName,
      String lastName,
      int cityId
  ) {
    create(
        new Person(
            0,
            firstName,
            lastName,
            cityId
        )
    );
  }

}