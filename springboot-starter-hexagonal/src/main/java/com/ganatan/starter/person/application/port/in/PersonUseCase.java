package com.ganatan.starter.person.application.port.in;

import com.ganatan.starter.person.domain.Person;
import java.util.List;

public interface PersonUseCase {

  List<Person> getAll();

  Person getById(int id);

  Person create(Person person);

  Person update(
      int id,
      Person person
  );

  void delete(int id);

}