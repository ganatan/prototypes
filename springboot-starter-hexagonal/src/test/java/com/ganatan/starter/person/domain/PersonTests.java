package com.ganatan.starter.person.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class PersonTests {

  @Test
  void personShouldExposeProperties() {

    Person person = new Person(
        1,
        "Steven",
        "Spielberg",
        1
    );

    assertNotNull(person);

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

    assertEquals(
        1,
        person.cityId()
    );

  }

  @Test
  void personShouldImplementEquality() {

    Person person1 = new Person(
        1,
        "Steven",
        "Spielberg",
        1
    );

    Person person2 = new Person(
        1,
        "Steven",
        "Spielberg",
        1
    );

    assertEquals(
        person1,
        person2
    );

  }

  @Test
  void personShouldBeDifferentWhenPropertiesDiffer() {

    Person person1 = new Person(
        1,
        "Steven",
        "Spielberg",
        1
    );

    Person person2 = new Person(
        2,
        "Martin",
        "Scorsese",
        2
    );

    assertNotEquals(
        person1,
        person2
    );

  }

}