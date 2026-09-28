package com.ganatan.starter.api.person;

import jakarta.validation.Valid;
import java.util.List;
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

  private final PersonService personService;

  public PersonController(
      PersonService personService
  ) {
    this.personService =
        personService;
  }

  @GetMapping
  public List<PersonOutputDto> getAllPersons() {
    return personService
        .getAllPersons()
        .stream()
        .map(PersonOutputDto::from)
        .toList();
  }

  @GetMapping("/{id}")
  public PersonOutputDto getPersonById(
      @PathVariable int id
  ) {
    Person person = personService
        .getPersonById(id)
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND
            )
        );

    return PersonOutputDto.from(
        person
    );
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PersonOutputDto createPerson(
      @Valid @RequestBody PersonInputDto input
  ) {
    Person person = personService
        .createPerson(
            input.firstName(),
            input.lastName(),
            input.cityId()
        );

    return PersonOutputDto.from(
        person
    );
  }

  @PutMapping("/{id}")
  public PersonOutputDto updatePerson(
      @PathVariable int id,
      @Valid @RequestBody PersonInputDto input
  ) {
    Person person = personService
        .updatePerson(
            id,
            input.firstName(),
            input.lastName(),
            input.cityId()
        )
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND
            )
        );

    return PersonOutputDto.from(
        person
    );
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deletePerson(
      @PathVariable int id
  ) {
    boolean deleted =
        personService.deletePerson(id);

    if (!deleted) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND
      );
    }
  }
}