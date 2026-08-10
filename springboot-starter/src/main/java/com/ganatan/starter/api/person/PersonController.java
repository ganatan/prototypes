package com.ganatan.starter.api.person;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/persons")
public class PersonController {

  private final AtomicInteger idCounter =
      new AtomicInteger(0);

  private final List<Person> personList =
      new ArrayList<>();

  public PersonController() {
    addInitialPerson("Steven", "Spielberg", 1);
    addInitialPerson("Martin", "Scorsese", 2);
    addInitialPerson("Francis", "Ford Coppola", 3);
    addInitialPerson("George", "Lucas", 4);
    addInitialPerson("Quentin", "Tarantino", 5);
    addInitialPerson("David", "Fincher", 6);
    addInitialPerson("Spike", "Lee", 7);
  }

  @GetMapping
  public List<PersonOutputDto> getAllPersons() {
    return personList.stream()
        .map(PersonOutputDto::from)
        .toList();
  }

  @GetMapping("/{id}")
  public PersonOutputDto getPersonById(
      @PathVariable int id
  ) {
    Person person = findPersonById(id)
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND
            )
        );

    return PersonOutputDto.from(person);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PersonOutputDto createPerson(
      @Valid @RequestBody PersonInputDto input
  ) {
    int id = idCounter.incrementAndGet();

    Person person = new Person(
        id,
        input.firstName(),
        input.lastName(),
        input.cityId()
    );

    personList.add(person);

    return PersonOutputDto.from(person);
  }

  @PutMapping("/{id}")
  public PersonOutputDto updatePerson(
      @PathVariable int id,
      @Valid @RequestBody PersonInputDto input
  ) {
    Person existing = findPersonById(id)
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND
            )
        );

    Person updated = new Person(
        existing.id(),
        input.firstName(),
        input.lastName(),
        input.cityId()
    );

    int index = personList.indexOf(existing);

    personList.set(index, updated);

    return PersonOutputDto.from(updated);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePerson(
      @PathVariable int id
  ) {
    Person existing = findPersonById(id)
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND
            )
        );

    personList.remove(existing);
  }

  private void addInitialPerson(
      String firstName,
      String lastName,
      int cityId
  ) {
    int id = idCounter.incrementAndGet();

    personList.add(
        new Person(
            id,
            firstName,
            lastName,
            cityId
        )
    );
  }

  private Optional<Person> findPersonById(
      int id
  ) {
    return personList.stream()
        .filter(person -> person.id() == id)
        .findFirst();
  }
}