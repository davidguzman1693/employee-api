package com.coding.challenge.service;

import com.coding.challenge.database.entity.EmployeeEntity;
import com.coding.challenge.database.repository.EmployeeRepository;
import com.coding.challenge.exception.EmployeeException;
import com.coding.challenge.exception.ValidationEmployeeException;
import com.coding.challenge.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

  private static final String TEST_ID = "testID";
  private static final String TEST_FULL_NAME = "test";
  private static final String TEST_EMAIL = "test@test.com";
  private static final LocalDate TEST_BIRTHDAY = LocalDate.of(1993, 12, 16);
  @Mock
  private EmployeeRepository employeeRepository;

  private EmployeeService testee;

  @BeforeEach
  void setUp() {
    testee = new EmployeeServiceImpl(employeeRepository);
  }

  @Test
  void createInvalidEmployeeEmailTest() {
    Employee requestEmployee = new Employee();
    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.create(requestEmployee));
    String expected = "Empty/Null email for the employee when creating it.";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void createInvalidEmployeeNoNameTest() {
    Employee requestEmployee = new Employee();
    requestEmployee.setEmail("test@test.gmail");
    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.create(requestEmployee));
    String expected = "Empty/Null full name for the employee when creating it.";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void createInvalidEmployeeNoBirthdayTest() {
    Employee requestEmployee = new Employee();
    requestEmployee.setEmail("test@test.gmail");
    requestEmployee.setFullName("Test");
    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.create(requestEmployee));
    String expected = "Not valid null birthday when creaing the employee";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void createEmployeeTest() {
    Employee requestEmployee = createMockEmployee();
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    doReturn(createMockEmployeeEntity()).when(employeeRepository).save(any(EmployeeEntity.class));
    Employee responseEmployee = testee.create(requestEmployee);
    assertEquals(TEST_ID, responseEmployee.getUuid());
    assertEquals(TEST_FULL_NAME, responseEmployee.getFullName());
    assertEquals(TEST_EMAIL, responseEmployee.getEmail());
    assertEquals(TEST_BIRTHDAY, responseEmployee.getBirthday());
    assertTrue(responseEmployee.getHobbies().isEmpty());
  }

  @Test
  void updateEmployeeNoUUIDTest() {
    Employee requestEmployee = mock(Employee.class);
    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.update(requestEmployee));
    String expected = "Invalid UUID on employee: null";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void updateEmployeeNoEmailTest() {
    Employee requestEmployee = mock(Employee.class);
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.update(requestEmployee));
    String expected = "Empty/Null email for the employee when creating it.";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void updateEmployeeNoFullNameTest() {
    Employee requestEmployee = mock(Employee.class);
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    doReturn(TEST_EMAIL).when(requestEmployee).getEmail();

    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.update(requestEmployee));
    String expected = "Empty/Null full name for the employee when creating it.";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void updateEmployeeNoBirthdayTest() {
    Employee requestEmployee = mock(Employee.class);
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    doReturn(TEST_EMAIL).when(requestEmployee).getEmail();
    doReturn(TEST_FULL_NAME).when(requestEmployee).getFullName();

    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.update(requestEmployee));
    String expected = "Not valid null birthday when creaing the employee";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void updateEmployeeTest() {
    Employee requestEmployee = createMockEmployee();
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    doReturn(createMockEmployeeEntity()).when(employeeRepository).save(any(EmployeeEntity.class));

    Employee responseEmployee = testee.update(requestEmployee);

    assertEquals(TEST_ID, responseEmployee.getUuid());
    assertEquals(TEST_FULL_NAME, responseEmployee.getFullName());
    assertEquals(TEST_EMAIL, responseEmployee.getEmail());
    assertEquals(TEST_BIRTHDAY, responseEmployee.getBirthday());
    assertTrue(responseEmployee.getHobbies().isEmpty());
  }

  @Test
  void getEmployeeByIdExceptionTest() {
    doReturn(Optional.empty()).when(employeeRepository).findById("123");
    Exception exception = assertThrows(EmployeeException.class,
        () -> testee.getById("123"));

    String expected = "Employee not found in database";
    assertEquals(expected, exception.getMessage());
  }

  @Test
  void getEmployeeByIdTest() {
    doReturn(Optional.of(createMockEmployeeEntity())).when(employeeRepository).findById("123");
    Employee responseEmployee = testee.getById("123");

    assertEquals(TEST_ID, responseEmployee.getUuid());
    assertEquals(TEST_FULL_NAME, responseEmployee.getFullName());
    assertEquals(TEST_EMAIL, responseEmployee.getEmail());
    assertEquals(TEST_BIRTHDAY, responseEmployee.getBirthday());
    assertTrue(responseEmployee.getHobbies().isEmpty());
  }

  @Test
  void getEmployeeListTest() {
    doReturn(List.of(createMockEmployeeEntity())).when(employeeRepository).findAll();
    List<Employee> listEmployee = testee.getListEmployee();

    assertEquals(1, listEmployee.size());
    Employee responseEmployee = listEmployee.get(0);
    assertEquals(TEST_ID, responseEmployee.getUuid());
    assertEquals(TEST_FULL_NAME, responseEmployee.getFullName());
    assertEquals(TEST_EMAIL, responseEmployee.getEmail());
    assertEquals(TEST_BIRTHDAY, responseEmployee.getBirthday());
    assertTrue(responseEmployee.getHobbies().isEmpty());
  }


  @Test
  void deleteEmployeeNoUUIDTest() {
    Employee requestEmployee = mock(Employee.class);
    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.delete(requestEmployee));
    String expected = "Invalid UUID on employee: null";
    assertEquals(expected, exception.getMessage());
    verify(employeeRepository, never()).delete(any(EmployeeEntity.class));
  }

  @Test
  void deleteEmployeeNoEmailTest() {
    Employee requestEmployee = mock(Employee.class);
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.delete(requestEmployee));
    String expected = "Empty/Null email for the employee when creating it.";
    assertEquals(expected, exception.getMessage());
    verify(employeeRepository, never()).delete(any(EmployeeEntity.class));
  }

  @Test
  void deleteEmployeeNoFullNameTest() {
    Employee requestEmployee = mock(Employee.class);
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    doReturn(TEST_EMAIL).when(requestEmployee).getEmail();

    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.delete(requestEmployee));
    String expected = "Empty/Null full name for the employee when creating it.";
    assertEquals(expected, exception.getMessage());
    verify(employeeRepository, never()).delete(any(EmployeeEntity.class));
  }

  @Test
  void deleteEmployeeNoBirthdayTest() {
    Employee requestEmployee = mock(Employee.class);
    doReturn(TEST_ID).when(requestEmployee).getUuid();
    doReturn(TEST_EMAIL).when(requestEmployee).getEmail();
    doReturn(TEST_FULL_NAME).when(requestEmployee).getFullName();

    Exception exception = assertThrows(ValidationEmployeeException.class,
        () -> testee.delete(requestEmployee));
    String expected = "Not valid null birthday when creaing the employee";
    assertEquals(expected, exception.getMessage());
    verify(employeeRepository, never()).delete(any(EmployeeEntity.class));
  }

  @Test
  void deleteEmployeeTest() {
    Employee employee = createMockEmployee();
    doReturn(TEST_ID).when(employee).getUuid();
    testee.delete(employee);
    verify(employeeRepository, times(1)).delete(any(EmployeeEntity.class));
  }

  private Employee createMockEmployee() {
    Employee employee = mock(Employee.class);

    doReturn(TEST_FULL_NAME).when(employee).getFullName();
    doReturn(TEST_EMAIL).when(employee).getEmail();
    doReturn(TEST_BIRTHDAY).when(employee).getBirthday();
    doReturn(Collections.EMPTY_SET).when(employee).getHobbies();

    return employee;
  }

  private EmployeeEntity createMockEmployeeEntity() {
    EmployeeEntity employeeEntity = mock(EmployeeEntity.class);

    doReturn(TEST_ID).when(employeeEntity).getUuid();
    doReturn(TEST_FULL_NAME).when(employeeEntity).getFullName();
    doReturn(TEST_EMAIL).when(employeeEntity).getEmail();
    doReturn(TEST_BIRTHDAY).when(employeeEntity).getBirthday();
    doReturn(Collections.EMPTY_SET).when(employeeEntity).getHobbies();

    return employeeEntity;
  }
}
