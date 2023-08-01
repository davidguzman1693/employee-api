package com.coding.challenge.database.repository;

import com.coding.challenge.database.entity.EmployeeEntity;
import com.coding.challenge.database.entity.HobbyEntity;
import com.coding.challenge.init.StreamInitializer;
import com.coding.challenge.model.Hobby;
import org.apache.tomcat.jni.Local;
import org.hibernate.dialect.Database;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@EntityScan(
    basePackages = "com.coding.challenge.database.entity"
)
@EnableAutoConfiguration
class EmployeeRepositoryTest {

  @Autowired
  private EmployeeRepository testee;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TransactionTemplate transactionTemplate;

  private List<EmployeeEntity> employeesWithHobbie;
  private List<EmployeeEntity> employeeWithoutHobbies;

  @BeforeEach
  void setUp() {
    setUpDbEntries();
  }

  @Test
  void testRepository() {
    List<EmployeeEntity> employeeList = testee.findAll();
    assertFalse(employeeList.isEmpty());
    assertEquals(8, employeeList.size());

    for(EmployeeEntity employee: employeeList){
      assertTrue(testee.findById(employee.getUuid()).isPresent());
    }

    //Delete random employee
    testee.delete(employeeList.get(0));

    employeeList = testee.findAll();
    assertFalse(employeeList.isEmpty());
    assertEquals(7, employeeList.size());

    //Updating first employee
    EmployeeEntity firstEmployeeEntity = employeeList.get(0);
    final String id = firstEmployeeEntity.getUuid();
    firstEmployeeEntity.setFullName("Edited name");

    testee.save(firstEmployeeEntity);

    employeeList = testee.findAll();
    assertFalse(employeeList.isEmpty());
    assertEquals(7, employeeList.size());

    Optional<EmployeeEntity> employeeEntityOpt = testee.findById(id);
    assertTrue(employeeEntityOpt.isPresent());
    EmployeeEntity employeeEntity = employeeEntityOpt.get();
    assertEquals("Edited name", employeeEntity.getFullName());
  }

  private void setUpDbEntries() {
    transactionTemplate.execute(t -> {
      createEmployeesWithNoHobbies();
      createEmployeesWithHobbies();

      return null;
    });
    entityManager.flush();
  }

  private void createEmployeesWithNoHobbies() {
    employeeWithoutHobbies = new ArrayList<>();
    for (int i = 1; i <= 4; i++) {
      EmployeeEntity employee = new EmployeeEntity();
      employee.setEmail("test" + i + "@test.com");
      employee.setFullName("test" + i);
      employee.setBirthday(LocalDate.of(1993, 12, i));
      employeeWithoutHobbies.add(employee);
      entityManager.persist(employee);
    }
  }

  private void createEmployeesWithHobbies() {
    employeesWithHobbie = new ArrayList<>();
    for (int i = 5; i <= 8; i++) {
      EmployeeEntity employee = new EmployeeEntity();
      employee.setEmail("test" + i + "@test.com");
      employee.setFullName("test" + i);
      employee.setBirthday(LocalDate.of(1993, 12, i));

      Set<HobbyEntity> hobbies = new HashSet<>();
      for (int j = 1; j <= 2; j++) {
        HobbyEntity hobbyEntity = new HobbyEntity();
        hobbyEntity.setName("Hobby" + i);
        hobbies.add(hobbyEntity);
      }
      employeesWithHobbie.add(employee);
      entityManager.persist(employee);
    }
  }
}
