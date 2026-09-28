package com.ganatan.starter.person.infrastructure.adapter.out.jpa;

import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.domain.Person;
import java.util.List;
import java.util.Optional;

public class JpaPersonRepositoryAdapter
    implements PersonRepository {

  private final SpringDataPersonRepository repository;

  public JpaPersonRepositoryAdapter(
      SpringDataPersonRepository repository
  ) {
    this.repository = repository;
  }

  @Override
  public List<Person> findAll() {
    return repository.findAll()
        .stream()
        .map(
            PersonEntity::toDomain
        )
        .toList();
  }

  @Override
  public Optional<Person> findById(
      int id
  ) {
    return repository.findById(
            id
        )
        .map(
            PersonEntity::toDomain
        );
  }

  @Override
  public Person create(
      Person person
  ) {
    PersonEntity entity =
        PersonEntity.fromDomain(
            person
        );

    PersonEntity saved =
        repository.save(
            entity
        );

    return saved.toDomain();
  }

  @Override
  public Person update(
      Person person
  ) {
    PersonEntity entity =
        repository.findById(
                person.id()
            )
            .orElseThrow();

    entity.updateFrom(
        person
    );

    PersonEntity saved =
        repository.save(
            entity
        );

    return saved.toDomain();
  }

  @Override
  public void deleteById(
      int id
  ) {
    repository.deleteById(
        id
    );
  }

}