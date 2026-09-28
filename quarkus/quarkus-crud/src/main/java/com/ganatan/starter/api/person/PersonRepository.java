package com.ganatan.starter.api.person;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class PersonRepository

    implements PanacheRepositoryBase<
    Person,
    Integer
    > {

  public List<Person> findAllByOrderByIdAsc() {

    return find(
        "ORDER BY id"
    ).list();

  }

}