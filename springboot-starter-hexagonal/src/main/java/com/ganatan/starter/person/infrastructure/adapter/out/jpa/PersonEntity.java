package com.ganatan.starter.person.infrastructure.adapter.out.jpa;

import com.ganatan.starter.person.domain.Person;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class PersonEntity {

  @Id
  @GeneratedValue(
      strategy = GenerationType.IDENTITY
  )
  private Integer id;

  @Column(
      name = "first_name",
      nullable = false,
      length = 50
  )
  private String firstName;

  @Column(
      name = "last_name",
      nullable = false,
      length = 50
  )
  private String lastName;

  @Column(
      name = "city_id",
      nullable = false
  )
  private int cityId;

  protected PersonEntity() {
  }

  public PersonEntity(
      String firstName,
      String lastName,
      int cityId
  ) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.cityId = cityId;
  }

  public Integer getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public int getCityId() {
    return cityId;
  }

  public void updateFrom(
      Person person
  ) {
    this.firstName =
        person.firstName();

    this.lastName =
        person.lastName();

    this.cityId =
        person.cityId();
  }

  public Person toDomain() {
    return new Person(
        id,
        firstName,
        lastName,
        cityId
    );
  }

  public static PersonEntity fromDomain(
      Person person
  ) {
    return new PersonEntity(
        person.firstName(),
        person.lastName(),
        person.cityId()
    );
  }

}