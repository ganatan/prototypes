package com.ganatan.starter.api.person;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "person")
public class Person {

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

  protected Person() {
  }

  public Person(
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

  public void setFirstName(
      String firstName
  ) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(
      String lastName
  ) {
    this.lastName = lastName;
  }

  public int getCityId() {
    return cityId;
  }

  public void setCityId(
      int cityId
  ) {
    this.cityId = cityId;
  }
}