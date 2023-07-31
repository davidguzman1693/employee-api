package com.coding.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories("com.coding.challenge.database.repository")
@EntityScan("com.coding.challenge.database.entity")
public class EmployeeServiceApp {

  public static void main(String... args) {
    SpringApplication.run(EmployeeServiceApp.class, args);
  }
}
