package com.ganatan.starter.api.person;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
class PersonControllerTests {

  @Test
  void getAllPersons_shouldReturnPersonOutputs() {
    given()
        .when()
        .get("/persons")
        .then()
        .statusCode(200)
        .body("[0].id", is(1))
        .body("[0].firstName", is("Steven"))
        .body("[0].lastName", is("Spielberg"))
        .body("[0].cityId", is(1));
  }

  @Test
  void getPersonById_shouldReturnPersonOutput() {
    given()
        .when()
        .get("/persons/1")
        .then()
        .statusCode(200)
        .body("id", is(1))
        .body("firstName", is("Steven"))
        .body("lastName", is("Spielberg"))
        .body("cityId", is(1));
  }

  @Test
  void createPerson_shouldReturnCreatedPerson() {
    String payload = """
      {
        "firstName": "Clint",
        "lastName": "Eastwood",
        "cityId": 8
      }
      """;

    given()
        .contentType(ContentType.JSON)
        .body(payload)
        .when()
        .post("/persons")
        .then()
        .statusCode(201)
        .body("id", is(8))
        .body("firstName", is("Clint"))
        .body("lastName", is("Eastwood"))
        .body("cityId", is(8));
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
        .contentType(ContentType.JSON)
        .body(payload)
        .when()
        .post("/persons")
        .then()
        .statusCode(400);
  }

  @Test
  void getPersonById_shouldReturnNotFound() {
    given()
        .when()
        .get("/persons/999")
        .then()
        .statusCode(404);
  }
}