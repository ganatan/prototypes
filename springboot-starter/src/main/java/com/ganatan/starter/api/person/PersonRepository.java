package com.ganatan.starter.api.person;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository
    extends JpaRepository<Person, Integer> {

  List<Person> findAllByOrderByIdAsc();
}