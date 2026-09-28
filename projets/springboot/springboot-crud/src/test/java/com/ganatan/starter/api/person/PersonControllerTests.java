package com.ganatan.starter.api.person;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

class PersonControllerTests {

  private MockMvc mockMvc;
  private PersonService service;

  @BeforeEach
  void setUp() {
    Validator validator = Validation
        .buildDefaultValidatorFactory()
        .getValidator();

    service =
        Mockito.mock(PersonService.class);

    PersonController controller =
        new PersonController(service);

    mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
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

    when(
        service.getAllPersons()
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
  void getPersonById_shouldReturnPerson()
      throws Exception {

    Person person =
        new Person(
            1,
            "Steven",
            "Spielberg",
            1
        );

    when(
        service.getPersonById(1)
    ).thenReturn(
        Optional.of(person)
    );

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
  void getPersonById_shouldReturnNotFound()
      throws Exception {

    when(
        service.getPersonById(999)
    ).thenReturn(
        Optional.empty()
    );

    mockMvc.perform(
            get("/persons/999")
        )
        .andExpect(
            status().isNotFound()
        );
  }

  @Test
  void createPerson_shouldReturnCreatedPerson()
      throws Exception {

    Person person =
        new Person(
            8,
            "Clint",
            "Eastwood",
            8
        );

    when(
        service.createPerson(
            anyString(),
            anyString(),
            anyInt()
        )
    ).thenReturn(person);

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
  void updatePerson_shouldReturnUpdatedPerson()
      throws Exception {

    Person person =
        new Person(
            1,
            "Steven",
            "Spielberg Updated",
            10
        );

    when(
        service.updatePerson(
            anyInt(),
            anyString(),
            anyString(),
            anyInt()
        )
    ).thenReturn(
        Optional.of(person)
    );

    String payload = """
      {
        "firstName": "Steven",
        "lastName": "Spielberg Updated",
        "cityId": 10
      }
      """;

    mockMvc.perform(
            put("/persons/1")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(payload)
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
                .value("Spielberg Updated")
        )
        .andExpect(
            jsonPath("$.cityId")
                .value(10)
        );
  }

  @Test
  void updatePerson_shouldReturnNotFound()
      throws Exception {

    when(
        service.updatePerson(
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

    mockMvc.perform(
            put("/persons/999")
                .contentType(
                    MediaType.APPLICATION_JSON
                )
                .content(payload)
        )
        .andExpect(
            status().isNotFound()
        );
  }

  @Test
  void deletePerson_shouldReturnNoContent()
      throws Exception {

    when(
        service.deletePerson(1)
    ).thenReturn(true);

    mockMvc.perform(
            delete("/persons/1")
        )
        .andExpect(
            status().isNoContent()
        );
  }

  @Test
  void deletePerson_shouldReturnNotFound()
      throws Exception {

    when(
        service.deletePerson(999)
    ).thenReturn(false);

    mockMvc.perform(
            delete("/persons/999")
        )
        .andExpect(
            status().isNotFound()
        );
  }
}