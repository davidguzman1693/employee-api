package com.coding.challenge.service;

import com.coding.challenge.database.entity.EmployeeEntity;
import com.coding.challenge.database.entity.HobbyEntity;
import com.coding.challenge.database.repository.EmployeeRepository;
import com.coding.challenge.exception.EmployeeException;
import com.coding.challenge.exception.ValidationEmployeeException;
import com.coding.challenge.model.Employee;
import com.coding.challenge.model.Hobby;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


@Service
public class EmployeeServiceImpl implements EmployeeService {

  private EmployeeRepository employeeRepository;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public Employee create(@Nonnull Employee employee) {
    validateEmployee(employee);
    EmployeeEntity createdEmployeeEntity = employeeRepository.save(convertToEmployeeEntity(employee));

    return convertToEmployeeModel(createdEmployeeEntity);
  }

  @Nullable
  @Override
  public Employee update(@Nonnull Employee employee) {
    validateExistingEmployee(employee);
    EmployeeEntity updateEmployee = employeeRepository.save(convertToEmployeeEntity(employee));
    return convertToEmployeeModel(updateEmployee);
  }

  @Nullable
  @Override
  public Employee getById(@Nonnull String id) {
    EmployeeEntity employee = employeeRepository.findById(id).orElseThrow(
        () -> new EmployeeException("Employee not found in database"));
    return convertToEmployeeModel(employee);
  }

  @Nonnull
  @Override
  public List<Employee> getListEmployee() {
    List<Employee> employeeList = new ArrayList<>();
    List<EmployeeEntity> employeeEntities = employeeRepository.findAll();
    for (EmployeeEntity employeeEntity : employeeEntities) {
      Employee employee = convertToEmployeeModel(employeeEntity);
      employeeList.add(employee);
    }
    return employeeList;
  }

  @Override
  public void delete(@Nonnull Employee employee) {
    validateExistingEmployee(employee);
    employeeRepository.delete(convertToEmployeeEntity(employee));
  }

  private static EmployeeEntity convertToEmployeeEntity(Employee employee) {
    EmployeeEntity employeeEntity = new EmployeeEntity();
    employeeEntity.setUuid(employee.getUuid());
    employeeEntity.setFullName(employee.getFullName());
    employeeEntity.setEmail(employee.getEmail());
    employeeEntity.setBirthday(employee.getBirthday());
    Set<HobbyEntity> hobbyEntityList = new TreeSet<>();
    for (Hobby hobby : employee.getHobbies()) {
      HobbyEntity hobbyEntity = new HobbyEntity();
      hobbyEntity.setId(hobby.getId());
      hobbyEntity.setName(hobby.getName());
      hobbyEntityList.add(hobbyEntity);
    }

    employeeEntity.setHobbies(hobbyEntityList);
    return employeeEntity;
  }

  private static Employee convertToEmployeeModel(EmployeeEntity employeeEntity) {
    Employee employeeModel = new Employee();
    employeeModel.setUuid(employeeEntity.getUuid());
    employeeModel.setFullName(employeeEntity.getFullName());
    employeeModel.setEmail(employeeEntity.getEmail());
    employeeModel.setBirthday(employeeEntity.getBirthday());
    Set<Hobby> hobbySet = new HashSet<>();
    for (HobbyEntity hobbyEntity : employeeEntity.getHobbies()) {
      Hobby hobby = new Hobby();
      hobby.setId(hobbyEntity.getId());
      hobby.setName(hobbyEntity.getName());
      hobbySet.add(hobby);
    }

    employeeModel.setHobbies(hobbySet);
    return employeeModel;
  }

  private static void validateExistingEmployee(@Nonnull Employee employee) {
    if (Strings.isBlank(employee.getUuid())) {
      throw new ValidationEmployeeException("Invalid UUID on employee: " + employee.getUuid());
    }

    validateEmployee(employee);
  }

  private static void validateEmployee(@Nonnull Employee employee) {
    if (Strings.isBlank(employee.getEmail())) {
      throw new ValidationEmployeeException("Empty/Null email for the employee when creating it.");
    }

    if (Strings.isBlank(employee.getFullName())) {
      throw new ValidationEmployeeException("Empty/Null full name for the employee when creating it.");
    }

    if (employee.getBirthday() == null) {
      throw new ValidationEmployeeException("Not valid null birthday when creaing the employee");
    }
  }

}
