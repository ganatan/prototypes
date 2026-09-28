package com.ganatan.starter.person.infrastructure.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ganatan.starter.person.application.port.in.PersonUseCase;
import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.application.service.PersonService;
import com.ganatan.starter.person.infrastructure.adapter.out.memory.InMemoryPersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PersonControllerTests {

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    PersonRepository repository =
        new InMemoryPersonRepository();

    PersonUseCase useCase =
        new PersonService(
            repository
        );

    PersonController controller =
        new PersonController(
            useCase
        );

    mockMvc =
        MockMvcBuilders
            .standaloneSetup(
                controller
            )
            .setControllerAdvice(
                new PersonExceptionHandler()
            )
            .build();
  }

  @Test
  void getAllShouldReturnSevenPersons()
      throws Exception {

    mockMvc.perform(
            get("/persons")
        )
        .andExpect(
            status().isOk()
        )
        .andExpect(
            jsonPath("$.length()")
                .value(7)
        )
        .andExpect(
            jsonPath("$[0].id")
                .value(1)
        )
        .andExpect(
            jsonPath("$[0].firstName")
                .value("Steven")
        )
        .andExpect(
            jsonPath("$[0].lastName")
                .value("Spielberg")
        )
        .andExpect(
            jsonPath("$[0].cityId")
                .value(1)
        );
  }

  @Test
  void getByIdShouldReturnPerson()
      throws Exception {

    mockMvc.perform(
            get("/persons/1")
        )
        .andExpect(
            status().isOk()
        )
        .andExpect(
            jsonPath("$.id")
                .value(1)
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
            jsonPath("$.cityId")
                .value(1)
        );
  }

  @Test
  void getByIdShouldReturnNotFound()
      throws Exception {

    mockMvc.perform(
            get("/persons/999")
        )
        .andExpect(
            status().isNotFound()
        );
  }

  @Test
  void createShouldReturnCreated()
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
                .content(
                    payload
                )
        )
        .andExpect(
            status().isCreated()
        )
        .andExpect(
            jsonPath("$.id")
                .value(8)
        )
        .andExpect(
            jsonPath("$.firstName")
                .value("Clint")
        )
        .andExpect(
            jsonPath("$.lastName")
                .value("Eastwood")
        )
        .andExpect(
            jsonPath("$.cityId")
                .value(8)
        );
  }

  @Test
  void updateShouldReturnUpdatedPerson()
      throws Exception {

    String payload = """
        {
          "firstName": "Steven",
          "lastName": "Spielberg",
          "cityId": 10
        }
        """;

    mockMvc.perform(
            put("/persons/1")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    payload
                )
        )
        .andExpect(
            status().isOk()
        )
        .andExpect(
            jsonPath("$.id")
                .value(1)
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
            jsonPath("$.cityId")
                .value(10)
        );
  }

  @Test
  void updateShouldReturnNotFound()
      throws Exception {

    String payload = """
        {
          "firstName": "Unknown",
          "lastName": "Person",
          "cityId": 1
        }
        """;

    mockMvc.perform(
            put("/persons/999")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(
                    payload
                )
        )
        .andExpect(
            status().isNotFound()
        );
  }

  @Test
  void deleteShouldReturnNoContent()
      throws Exception {

    mockMvc.perform(
            delete("/persons/1")
        )
        .andExpect(
            status().isNoContent()
        )
        .andExpect(
            content().string("")
        );
  }

  @Test
  void deleteShouldReturnNotFound()
      throws Exception {

    mockMvc.perform(
            delete("/persons/999")
        )
        .andExpect(
            status().isNotFound()
        );
  }

}