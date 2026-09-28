package com.ganatan.starter.person.infrastructure.adapter.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPersonRepository
    extends JpaRepository<PersonEntity, Integer> {
}