package com.ganatan.starter.person.infrastructure.adapter.out.postgresql;

import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.domain.Person;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class PostgreSqlPersonRepositoryAdapter implements PersonRepository {

  private static final String SQL_FIND_ALL = """
      SELECT
        id,
        first_name,
        last_name,
        city_id
      FROM person
      """;

  private static final String SQL_FIND_BY_ID = """
      SELECT
        id,
        first_name,
        last_name,
        city_id
      FROM person
      WHERE id = ?
      """;

  private static final String SQL_CREATE = """
      INSERT INTO person (
        first_name,
        last_name,
        city_id
      )
      VALUES (?, ?, ?)
      RETURNING
        id,
        first_name,
        last_name,
        city_id
      """;

  private static final String SQL_UPDATE = """
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

  private static final String SQL_DELETE = """
      DELETE FROM person
      WHERE id = ?
      """;

  private final JdbcTemplate jdbcTemplate;

  public PostgreSqlPersonRepositoryAdapter(
      JdbcTemplate jdbcTemplate
  ) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Person> findAll() {
    return jdbcTemplate.query(
        SQL_FIND_ALL,
        (resultSet, rowNumber) ->
            new Person(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getInt("city_id")
            )
    );
  }

  @Override
  public Optional<Person> findById(
      int id
  ) {
    return jdbcTemplate.query(
            SQL_FIND_BY_ID,
            (resultSet, rowNumber) ->
                new Person(
                    resultSet.getInt("id"),
                    resultSet.getString("first_name"),
                    resultSet.getString("last_name"),
                    resultSet.getInt("city_id")
                ),
            id
        )
        .stream()
        .findFirst();
  }

  @Override
  public Person create(
      Person person
  ) {
    return jdbcTemplate.queryForObject(
        SQL_CREATE,
        (resultSet, rowNumber) ->
            new Person(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getInt("city_id")
            ),
        person.firstName(),
        person.lastName(),
        person.cityId()
    );
  }

  @Override
  public Person update(
      Person person
  ) {
    return jdbcTemplate.queryForObject(
        SQL_UPDATE,
        (resultSet, rowNumber) ->
            new Person(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getInt("city_id")
            ),
        person.firstName(),
        person.lastName(),
        person.cityId(),
        person.id()
    );
  }

  @Override
  public void deleteById(
      int id
  ) {
    jdbcTemplate.update(
        SQL_DELETE,
        id
    );
  }

}