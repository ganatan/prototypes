package com.ganatan.starter.api.person;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PersonControllerTests {

  @InjectMock
  PersonService personService;

  @Test
  void getAllPersons_shouldReturnSevenPersons() {

    when(
        personService.getAllPersons()
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
            ),
            new Person(
                3,
                "Francis",
                "Ford Coppola",
                3
            ),
            new Person(
                4,
                "George",
                "Lucas",
                4
            ),
            new Person(
                5,
                "Quentin",
                "Tarantino",
                5
            ),
            new Person(
                6,
                "David",
                "Fincher",
                6
            ),
            new Person(
                7,
                "Spike",
                "Lee",
                7
            )
        )
    );

    given()
        .when()
        .get("/persons")
        .then()
        .statusCode(200)
        .body("$", hasSize(7));

  }

  @Test
  void getPersonById_shouldReturnPerson() {

    when(
        personService.getPersonById(1)
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

    given()
        .when()
        .get("/persons/1")
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body(
            "firstName",
            is("Steven")
        )
        .body(
            "lastName",
            is("Spielberg")
        )
        .body(
            "cityId",
            is(1)
        );

  }

  @Test
  void getPersonById_shouldReturnNotFound() {

    when(
        personService.getPersonById(999)
    ).thenReturn(
        Optional.empty()
    );

    given()
        .when()
        .get("/persons/999")
        .then()
        .statusCode(404);

  }

  @Test
  void createPerson_shouldReturnCreatedPerson() {

    when(
        personService.createPerson(
            anyString(),
            anyString(),
            anyInt()
        )
    ).thenReturn(
        new Person(
            8,
            "Clint",
            "Eastwood",
            8
        )
    );

    String payload = """
      {
        "firstName": "Clint",
        "lastName": "Eastwood",
        "cityId": 8
      }
      """;

    given()
        .contentType(
            ContentType.JSON
        )
        .body(payload)
        .when()
        .post("/persons")
        .then()
        .statusCode(201)
        .body("id", is(8))
        .body(
            "firstName",
            is("Clint")
        )
        .body(
            "lastName",
            is("Eastwood")
        )
        .body(
            "cityId",
            is(8)
        );

  }

  @Test
  void createPerson_shouldRejectInvalidPayload() {

    String payload = """
      {
        "firstName": "",
        "lastName": "Eastwood",
        "cityId": 0
      }
      """;

    given()
        .contentType(
            ContentType.JSON
        )
        .body(payload)
        .when()
        .post("/persons")
        .then()
        .statusCode(400);

  }

  @Test
  void updatePerson_shouldReturnUpdatedPerson() {

    when(
        personService.updatePerson(
            anyInt(),
            anyString(),
            anyString(),
            anyInt()
        )
    ).thenReturn(
        Optional.of(
            new Person(
                1,
                "Steven",
                "Spielberg",
                10
            )
        )
    );

    String payload = """
      {
        "firstName": "Steven",
        "lastName": "Spielberg",
        "cityId": 10
      }
      """;

    given()
        .contentType(
            ContentType.JSON
        )
        .body(payload)
        .when()
        .put("/persons/1")
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body(
            "firstName",
            is("Steven")
        )
        .body(
            "lastName",
            is("Spielberg")
        )
        .body(
            "cityId",
            is(10)
        );

  }

  @Test
  void updatePerson_shouldReturnNotFound() {

    when(
        personService.updatePerson(
            anyInt(),
            anyString(),
            anyString(),
            anyInt()
        )
    ).thenReturn(
        Optional.empty()
    );

    String payload = """
      {
        "firstName": "Unknown",
        "lastName": "Person",
        "cityId": 1
      }
      """;

    given()
        .contentType(
            ContentType.JSON
        )
        .body(payload)
        .when()
        .put("/persons/999")
        .then()
        .statusCode(404);

  }

  @Test
  void deletePerson_shouldReturnNoContent() {

    when(
        personService.deletePerson(1)
    ).thenReturn(
        true
    );

    given()
        .when()
        .delete("/persons/1")
        .then()
        .statusCode(204);

  }

  @Test
  void deletePerson_shouldReturnNotFound() {

    when(
        personService.deletePerson(999)
    ).thenReturn(
        false
    );

    given()
        .when()
        .delete("/persons/999")
        .then()
        .statusCode(404);

  }

}