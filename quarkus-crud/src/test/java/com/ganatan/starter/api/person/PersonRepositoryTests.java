package com.ganatan.starter.api.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.agroal.api.AgroalDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

class PersonRepositoryTests {

  private AgroalDataSource dataSource;

  private Connection connection;

  private PreparedStatement statement;

  private ResultSet resultSet;

  private PersonRepository repository;

  @BeforeEach
  void setUp() throws Exception {

    dataSource =
        Mockito.mock(
            AgroalDataSource.class
        );

    connection =
        Mockito.mock(
            Connection.class
        );

    statement =
        Mockito.mock(
            PreparedStatement.class
        );

    resultSet =
        Mockito.mock(
            ResultSet.class
        );

    when(
        dataSource.getConnection()
    ).thenReturn(
        connection
    );

    when(
        connection.prepareStatement(
            anyString()
        )
    ).thenReturn(
        statement
    );

    repository =
        new PersonRepository(
            dataSource
        );

  }

  @Test
  void findAll_shouldReturnPersons()
      throws Exception {

    when(
        statement.executeQuery()
    ).thenReturn(
        resultSet
    );

    when(
        resultSet.next()
    ).thenReturn(
        true,
        true,
        false
    );

    when(
        resultSet.getInt("id")
    ).thenReturn(
        1,
        2
    );

    when(
        resultSet.getString("first_name")
    ).thenReturn(
        "Steven",
        "Martin"
    );

    when(
        resultSet.getString("last_name")
    ).thenReturn(
        "Spielberg",
        "Scorsese"
    );

    when(
        resultSet.getInt("city_id")
    ).thenReturn(
        1,
        2
    );

    List<Person> persons =
        repository.findAll();

    assertEquals(
        2,
        persons.size()
    );

    assertEquals(
        "Steven",
        persons.get(0).firstName()
    );

    assertEquals(
        "Martin",
        persons.get(1).firstName()
    );

  }

  @Test
  void findById_shouldReturnPerson()
      throws Exception {

    when(
        statement.executeQuery()
    ).thenReturn(
        resultSet
    );

    when(
        resultSet.next()
    ).thenReturn(
        true
    );

    when(
        resultSet.getInt("id")
    ).thenReturn(
        1
    );

    when(
        resultSet.getString("first_name")
    ).thenReturn(
        "Steven"
    );

    when(
        resultSet.getString("last_name")
    ).thenReturn(
        "Spielberg"
    );

    when(
        resultSet.getInt("city_id")
    ).thenReturn(
        1
    );

    Person person = repository
        .findById(1)
        .orElseThrow();

    assertEquals(
        1,
        person.id()
    );

    assertEquals(
        "Steven",
        person.firstName()
    );

    assertEquals(
        "Spielberg",
        person.lastName()
    );

    verify(
        statement
    ).setInt(
        1,
        1
    );

  }

  @Test
  void findById_shouldReturnEmpty()
      throws Exception {

    when(
        statement.executeQuery()
    ).thenReturn(
        resultSet
    );

    when(
        resultSet.next()
    ).thenReturn(
        false
    );

    Optional<Person> person =
        repository.findById(999);

    assertTrue(
        person.isEmpty()
    );

  }

  @Test
  void create_shouldReturnCreatedPerson()
      throws Exception {

    when(
        statement.executeQuery()
    ).thenReturn(
        resultSet
    );

    when(
        resultSet.next()
    ).thenReturn(
        true
    );

    when(
        resultSet.getInt("id")
    ).thenReturn(
        8
    );

    when(
        resultSet.getString("first_name")
    ).thenReturn(
        "Clint"
    );

    when(
        resultSet.getString("last_name")
    ).thenReturn(
        "Eastwood"
    );

    when(
        resultSet.getInt("city_id")
    ).thenReturn(
        8
    );

    Person person =
        repository.create(
            "Clint",
            "Eastwood",
            8
        );

    assertEquals(
        8,
        person.id()
    );

    assertEquals(
        "Clint",
        person.firstName()
    );

    assertEquals(
        "Eastwood",
        person.lastName()
    );

    assertEquals(
        8,
        person.cityId()
    );

    verify(
        statement
    ).setString(
        1,
        "Clint"
    );

    verify(
        statement
    ).setString(
        2,
        "Eastwood"
    );

    verify(
        statement
    ).setInt(
        3,
        8
    );

  }

  @Test
  void update_shouldReturnUpdatedPerson()
      throws Exception {

    when(
        statement.executeQuery()
    ).thenReturn(
        resultSet
    );

    when(
        resultSet.next()
    ).thenReturn(
        true
    );

    when(
        resultSet.getInt("id")
    ).thenReturn(
        1
    );

    when(
        resultSet.getString("first_name")
    ).thenReturn(
        "Steven"
    );

    when(
        resultSet.getString("last_name")
    ).thenReturn(
        "Spielberg Updated"
    );

    when(
        resultSet.getInt("city_id")
    ).thenReturn(
        10
    );

    Person person = repository
        .update(
            1,
            "Steven",
            "Spielberg Updated",
            10
        )
        .orElseThrow();

    assertEquals(
        1,
        person.id()
    );

    assertEquals(
        "Spielberg Updated",
        person.lastName()
    );

    assertEquals(
        10,
        person.cityId()
    );

  }

  @Test
  void update_shouldReturnEmpty()
      throws Exception {

    when(
        statement.executeQuery()
    ).thenReturn(
        resultSet
    );

    when(
        resultSet.next()
    ).thenReturn(
        false
    );

    Optional<Person> person =
        repository.update(
            999,
            "Unknown",
            "Person",
            1
        );

    assertTrue(
        person.isEmpty()
    );

  }

  @Test
  void delete_shouldReturnTrue()
      throws Exception {

    when(
        statement.executeUpdate()
    ).thenReturn(
        1
    );

    boolean deleted =
        repository.delete(1);

    assertTrue(
        deleted
    );

    verify(
        statement
    ).setInt(
        1,
        1
    );

  }

  @Test
  void delete_shouldReturnFalse()
      throws Exception {

    when(
        statement.executeUpdate()
    ).thenReturn(
        0
    );

    boolean deleted =
        repository.delete(999);

    assertFalse(
        deleted
    );

  }

}