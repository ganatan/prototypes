package com.ganatan.starter.person.application.port.out;

import com.ganatan.starter.person.domain.Person;
import java.util.List;
import java.util.Optional;

public interface PersonRepository {

  List<Person> findAll();

  Optional<Person> findById(int id);

  Person create(Person person);

  Person update(Person person);

  void deleteById(int id);

}