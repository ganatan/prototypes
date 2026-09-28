package com.ganatan.starter.person.domain;

public record Person(
    int id,
    String firstName,
    String lastName,
    int cityId
) {
}