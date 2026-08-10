package com.ganatan.starter.api.person;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

class PersonControllerTests {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    Validator validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();

    mockMvc = MockMvcBuilders
        .standaloneSetup(
            new PersonController()
        )
        .setValidator(
            new SpringValidatorAdapter(
                validator
            )
        )
        .build();
  }

  @Test
  void getAllPersons_shouldReturnSevenPersons()
      throws Exception {

    mockMvc.perform(
            get("/persons")
        )
        .andExpect(
            status().isOk()
        )
        .andExpect(
            jsonPath("$", hasSize(7))
        );
  }

  @Test
  void getPersonById_shouldReturnPersonOutput()
      throws Exception {

    mockMvc.perform(
            get("/persons/1")
        )
        .andExpect(
            status().isOk()
        )
        .andExpect(
            jsonPath("$.id").value(1)
        )
        .andExpect(
            jsonPath("$.firstName")
                .value("Steven")
        )
        .andExpect(
            jsonPath("$.lastName")
                .value("Spielberg")
        )
        .andExpect(
            jsonPath("$.cityId").value(1)
        );
  }

  @Test
  void createPerson_shouldReturnCreatedPerson()
      throws Exception {

    String payload = """
      {
        "firstName": "Clint",
        "lastName": "Eastwood",
        "cityId": 8
      }
      """;

    mockMvc.perform(
            post("/persons")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(payload)
        )
        .andExpect(
            status().isCreated()
        )
        .andExpect(
            jsonPath("$.id").value(8)
        )
        .andExpect(
            jsonPath("$.firstName")
                .value("Clint")
        );
  }

  @Test
  void createPerson_shouldRejectInvalidPayload()
      throws Exception {

    String payload = """
      {
        "firstName": "",
        "lastName": "Eastwood",
        "cityId": 0
      }
      """;

    mockMvc.perform(
            post("/persons")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(payload)
        )
        .andExpect(
            status().isBadRequest()
        );
  }

  @Test
  void getPersonById_shouldReturnNotFound()
      throws Exception {

    mockMvc.perform(
            get("/persons/999")
        )
        .andExpect(
            status().isNotFound()
        );
  }
}