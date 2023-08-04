package com.coding.challenge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.coding.challenge")
public class EmployeeConsumerApp {

  public static void main(String[] args) {
    SpringApplication.run(EmployeeConsumerApp.class, args);
  }
}
