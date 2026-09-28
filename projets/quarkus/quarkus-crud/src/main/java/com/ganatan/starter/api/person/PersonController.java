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
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/persons")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {

  private final PersonService personService;

  public PersonController(
      PersonService personService
  ) {

    this.personService =
        personService;

  }

  @GET
  public List<PersonOutputDto> getAllPersons() {

    return personService
        .getAllPersons()
        .stream()
        .map(PersonOutputDto::from)
        .toList();

  }

  @GET
  @Path("/{id}")
  public PersonOutputDto getPersonById(
      @PathParam("id") int id
  ) {

    Person person = personService
        .getPersonById(id)
        .orElseThrow(
            NotFoundException::new
        );

    return PersonOutputDto.from(
        person
    );

  }

  @POST
  @ResponseStatus(201)
  public PersonOutputDto createPerson(
      @Valid PersonInputDto input
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

  @PUT
  @Path("/{id}")
  public PersonOutputDto updatePerson(
      @PathParam("id") int id,
      @Valid PersonInputDto input
  ) {

    Person person = personService
        .updatePerson(
            id,
            input.firstName(),
            input.lastName(),
            input.cityId()
        )
        .orElseThrow(
            NotFoundException::new
        );

    return PersonOutputDto.from(
        person
    );

  }

  @DELETE
  @Path("/{id}")
  @ResponseStatus(204)
  public void deletePerson(
      @PathParam("id") int id
  ) {

    boolean deleted =
        personService.deletePerson(id);

    if (!deleted) {
      throw new NotFoundException();
    }

  }

}