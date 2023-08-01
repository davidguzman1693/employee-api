package com.coding.challenge;

import com.coding.challenge.init.StreamInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.coding.challenge")//Scan clases on the base package
@EnableAutoConfiguration
@EnableJpaRepositories("com.coding.challenge.database.repository")
@EntityScan("com.coding.challenge.database.entity")
public class EmployeeServiceApp {

  private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceApp.class);
  public static void main(String... args) {
    SpringApplication.run(EmployeeServiceApp.class, args);

  }
}
