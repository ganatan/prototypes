package com.ganatan.starter.person.infrastructure.config;

import com.ganatan.starter.person.application.port.in.PersonUseCase;
import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.application.service.PersonService;
import com.ganatan.starter.person.infrastructure.adapter.out.jpa.JpaPersonRepositoryAdapter;
import com.ganatan.starter.person.infrastructure.adapter.out.jpa.SpringDataPersonRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersonConfiguration {

  @Bean
  public PersonRepository personRepository(
      SpringDataPersonRepository repository
  ) {
    return new JpaPersonRepositoryAdapter(
        repository
    );
  }

  @Bean
  public PersonUseCase personUseCase(
      PersonRepository repository
  ) {
    return new PersonService(
        repository
    );
  }

}

/*
package com.ganatan.starter.person.infrastructure.config;

import com.ganatan.starter.person.application.port.in.PersonUseCase;
import com.ganatan.starter.person.application.port.out.PersonRepository;
import com.ganatan.starter.person.application.service.PersonService;
import com.ganatan.starter.person.infrastructure.adapter.out.postgresql.PostgreSqlPersonRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class PersonConfiguration {

  @Bean
  public PersonRepository personRepository(
      JdbcTemplate jdbcTemplate
  ) {
    return new PostgreSqlPersonRepositoryAdapter(
        jdbcTemplate
    );
  }

  @Bean
  public PersonUseCase personUseCase(
      PersonRepository repository
  ) {
    return new PersonService(
        repository
    );
  }

}*/
