package com.coding.challenge.swagger;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.spring.data.rest.configuration.SpringDataRestConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Import(SpringDataRestConfiguration.class)
@Configuration
public class SpringFoxConfig {
  public static final String EMPLOYEE_TAG = "Employee service";
}
