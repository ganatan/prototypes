package com.ganatan.starter.api.root;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RootControllerTest {

  @Test
  void shouldReturnApplicationInformation() {

    given()
        .when()
        .get("/")
        .then()
        .statusCode(200)
        .body(
            "application",
            is("quarkus-starter")
        )
        .body(
            "status",
            is("running")
        );

  }

}