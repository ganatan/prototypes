package com.ganatan.starter.api.person;

import io.agroal.api.AgroalDataSource;

import jakarta.enterprise.context.ApplicationScoped;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PersonRepository {

  private final AgroalDataSource dataSource;

  public PersonRepository(
      AgroalDataSource dataSource
  ) {

    this.dataSource =
        dataSource;

  }

  public List<Person> findAll() {

    String sql = """
        SELECT
          id,
          first_name,
          last_name,
          city_id
        FROM person
        ORDER BY id
        """;

    try (
        Connection connection =
            dataSource.getConnection();

        PreparedStatement statement =
            connection.prepareStatement(sql);

        ResultSet resultSet =
            statement.executeQuery()
    ) {

      List<Person> persons =
          new ArrayList<>();

      while (resultSet.next()) {

        persons.add(
            mapPerson(resultSet)
        );

      }

      return persons;

    } catch (SQLException exception) {

      throw new IllegalStateException(
          exception
      );

    }

  }

  public Optional<Person> findById(
      int id
  ) {

    String sql = """
        SELECT
          id,
          first_name,
          last_name,
          city_id
        FROM person
        WHERE id = ?
        """;

    try (
        Connection connection =
            dataSource.getConnection();

        PreparedStatement statement =
            connection.prepareStatement(sql)
    ) {

      statement.setInt(
          1,
          id
      );

      try (
          ResultSet resultSet =
              statement.executeQuery()
      ) {

        if (!resultSet.next()) {

          return Optional.empty();

        }

        return Optional.of(
            mapPerson(resultSet)
        );

      }

    } catch (SQLException exception) {

      throw new IllegalStateException(
          exception
      );

    }

  }

  public Person create(
      String firstName,
      String lastName,
      int cityId
  ) {

    String sql = """
        INSERT INTO person (
          first_name,
          last_name,
          city_id
        )
        VALUES (
          ?,
          ?,
          ?
        )
        RETURNING
          id,
          first_name,
          last_name,
          city_id
        """;

    try (
        Connection connection =
            dataSource.getConnection();

        PreparedStatement statement =
            connection.prepareStatement(sql)
    ) {

      statement.setString(
          1,
          firstName
      );

      statement.setString(
          2,
          lastName
      );

      statement.setInt(
          3,
          cityId
      );

      try (
          ResultSet resultSet =
              statement.executeQuery()
      ) {

        if (!resultSet.next()) {

          throw new IllegalStateException();

        }

        return mapPerson(
            resultSet
        );

      }

    } catch (SQLException exception) {

      throw new IllegalStateException(
          exception
      );

    }

  }

  public Optional<Person> update(
      int id,
      String firstName,
      String lastName,
      int cityId
  ) {

    String sql = """
        UPDATE person
        SET
          first_name = ?,
          last_name = ?,
          city_id = ?
        WHERE id = ?
        RETURNING
          id,
          first_name,
          last_name,
          city_id
        """;

    try (
        Connection connection =
            dataSource.getConnection();

        PreparedStatement statement =
            connection.prepareStatement(sql)
    ) {

      statement.setString(
          1,
          firstName
      );

      statement.setString(
          2,
          lastName
      );

      statement.setInt(
          3,
          cityId
      );

      statement.setInt(
          4,
          id
      );

      try (
          ResultSet resultSet =
              statement.executeQuery()
      ) {

        if (!resultSet.next()) {

          return Optional.empty();

        }

        return Optional.of(
            mapPerson(resultSet)
        );

      }

    } catch (SQLException exception) {

      throw new IllegalStateException(
          exception
      );

    }

  }

  public boolean delete(
      int id
  ) {

    String sql = """
        DELETE FROM person
        WHERE id = ?
        """;

    try (
        Connection connection =
            dataSource.getConnection();

        PreparedStatement statement =
            connection.prepareStatement(sql)
    ) {

      statement.setInt(
          1,
          id
      );

      int rows =
          statement.executeUpdate();

      return rows > 0;

    } catch (SQLException exception) {

      throw new IllegalStateException(
          exception
      );

    }

  }

  private Person mapPerson(
      ResultSet resultSet
  ) throws SQLException {

    return new Person(
        resultSet.getInt("id"),
        resultSet.getString("first_name"),
        resultSet.getString("last_name"),
        resultSet.getInt("city_id")
    );

  }

}