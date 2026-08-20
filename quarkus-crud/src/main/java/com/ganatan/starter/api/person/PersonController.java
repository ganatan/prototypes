package com.ganatan.starter.api.person;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/persons")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {

  private final AtomicInteger idCounter =
      new AtomicInteger(0);

  private final List<Person> personList =
      new CopyOnWriteArrayList<>();

  public PersonController() {
    addInitialPerson("Steven", "Spielberg", 1);
    addInitialPerson("Martin", "Scorsese", 2);
    addInitialPerson("Francis", "Ford Coppola", 3);
    addInitialPerson("George", "Lucas", 4);
    addInitialPerson("Quentin", "Tarantino", 5);
    addInitialPerson("David", "Fincher", 6);
    addInitialPerson("Spike", "Lee", 7);
  }

  @GET
  public List<PersonOutputDto> getAllPersons() {
    return personList.stream()
        .map(PersonOutputDto::from)
        .toList();
  }

  @GET
  @Path("/{id}")
  public PersonOutputDto getPersonById(
      @PathParam("id") int id
  ) {
    Person person = findPersonById(id)
        .orElseThrow(NotFoundException::new);

    return PersonOutputDto.from(person);
  }

  @POST
  @ResponseStatus(201)
  public PersonOutputDto createPerson(
      @Valid PersonInputDto input
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

  @PUT
  @Path("/{id}")
  public PersonOutputDto updatePerson(
      @PathParam("id") int id,
      @Valid PersonInputDto input
  ) {
    Person existing = findPersonById(id)
        .orElseThrow(NotFoundException::new);

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

  @DELETE
  @Path("/{id}")
  @ResponseStatus(204)
  public void deletePerson(
      @PathParam("id") int id
  ) {
    Person existing = findPersonById(id)
        .orElseThrow(NotFoundException::new);

    personList.remove(existing);
  }

  private void addInitialPerson(
      String firstName,
      String lastName,
      int cityId
  ) {
    int id = idCounter.incrementAndGet();

    Person person = new Person(
        id,
        firstName,
        lastName,
        cityId
    );

    personList.add(person);
  }

  private Optional<Person> findPersonById(
      int id
  ) {
    return personList.stream()
        .filter(person -> person.id() == id)
        .findFirst();
  }
}