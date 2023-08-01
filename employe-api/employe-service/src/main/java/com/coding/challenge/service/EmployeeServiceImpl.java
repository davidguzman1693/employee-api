package com.coding.challenge.service;

import com.coding.challenge.config.KafkaConfigData;
import com.coding.challenge.database.entity.EmployeeEntity;
import com.coding.challenge.database.entity.HobbyEntity;
import com.coding.challenge.database.repository.EmployeeRepository;
import com.coding.challenge.database.repository.HobbyRepository;
import com.coding.challenge.exception.NotFoundEmployeeException;
import com.coding.challenge.exception.ValidationEmployeeException;
import com.coding.challenge.init.StreamInitializer;
import com.coding.challenge.kafka.avro.model.EmployeeAvroModel;
import com.coding.challenge.model.Employee;
import com.coding.challenge.model.Hobby;
import com.coding.challenge.transformer.AvroTransformer;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class EmployeeServiceImpl implements EmployeeService {

  private EmployeeRepository employeeRepository;

  private HobbyRepository hobbyRepository;

  private final AvroTransformer avroTransformer;

  private final EmployeeKafkaProducer employeeKafkaProducer;

  private final KafkaConfigData kafkaConfigData;

  @Autowired
  private StreamInitializer streamInitializer;

  public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                             HobbyRepository hobbyRepository,
                             AvroTransformer avroTransformer,
                             EmployeeKafkaProducer employeeKafkaProducer,
                             KafkaConfigData kafkaConfigData) {
    this.employeeRepository = employeeRepository;
    this.hobbyRepository = hobbyRepository;
    this.avroTransformer = avroTransformer;
    this.employeeKafkaProducer = employeeKafkaProducer;
    this.kafkaConfigData = kafkaConfigData;
  }

  @Override
  public Employee create(@Nonnull Employee employee) {
    validateEmployee(employee, EmployeeOperation.CREATE);
    EmployeeEntity createdEmployeeEntity = employeeRepository.save(convertToEmployeeEntity(employee));
    Employee createdEmployee = convertToEmployeeModel(createdEmployeeEntity);
    sendEmployeeToKafka(createdEmployee, EmployeeOperation.CREATE);

    return createdEmployee;
  }

  @Nullable
  @Override
  public Employee update(@Nonnull Employee employee) {
    validateExistingEmployee(employee);
    EmployeeEntity updateEmployee = employeeRepository.save(convertToEmployeeEntity(employee));
    sendEmployeeToKafka(employee, EmployeeOperation.UPDATE);
    return convertToEmployeeModel(updateEmployee);
  }

  @Nullable
  @Override
  public Employee getById(@Nonnull String id) {
    EmployeeEntity employee = employeeRepository.findById(id).orElseThrow(
        () -> new NotFoundEmployeeException("Employee not found in database"));
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
    sendEmployeeToKafka(employee, EmployeeOperation.DELETE);
    employeeRepository.delete(convertToEmployeeEntity(employee));
  }

  private EmployeeEntity convertToEmployeeEntity(Employee employee) {
    EmployeeEntity employeeEntity = new EmployeeEntity();
    employeeEntity.setUuid(employee.getUuid());
    employeeEntity.setFullName(employee.getFullName());
    employeeEntity.setEmail(employee.getEmail());
    employeeEntity.setBirthday(employee.getBirthday());
    Set<HobbyEntity> hobbyEntityList = new HashSet<>();
    for (Hobby hobby : employee.getHobbies()) {
      if (!Strings.isBlank(hobby.getName())) {
        HobbyEntity hobbyEntity = hobbyRepository.findByName(hobby.getName());
        if (hobbyEntity == null) {
          HobbyEntity hobbyEntityToCreate = new HobbyEntity();
          hobbyEntityToCreate.setName(hobby.getName());
          hobbyEntityList.add(hobbyEntityToCreate);
          continue;
        }
        hobbyEntityList.add(hobbyEntity);
      }
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

  private void validateExistingEmployee(@Nonnull Employee employee) {
    if (Strings.isBlank(employee.getUuid())) {
      throw new ValidationEmployeeException("Invalid UUID on employee: " + employee.getUuid());
    }

    validateEmployee(employee, EmployeeOperation.UPDATE);
  }

  private void validateEmployee(@Nonnull Employee employee,
                                EmployeeOperation operation) {
    if (Strings.isBlank(employee.getEmail())) {
      throw new ValidationEmployeeException("Empty/Null email for the employee when creating it.");
    }

    List<EmployeeEntity> employeeWithSameEmails = employeeRepository.findByEmail(employee.getEmail());

    if (employeeWithSameEmails != null && isEmailAlreadyUsed(operation, employeeWithSameEmails)) {
      throw new ValidationEmployeeException("Email is already in use.");
    }


    if (Strings.isBlank(employee.getFullName())) {
      throw new ValidationEmployeeException("Empty/Null full name for the employee when creating it.");
    }

    if (employee.getBirthday() == null) {
      throw new ValidationEmployeeException("Not valid null birthday when creaing the employee");
    }
  }

  private static boolean isEmailAlreadyUsed(EmployeeOperation operation, List<EmployeeEntity> employeeWithSameEmails) {
    if (operation == EmployeeOperation.CREATE && !employeeWithSameEmails.isEmpty()) {
      return true;
    }

    if (operation == EmployeeOperation.UPDATE && employeeWithSameEmails.size() > 1) {
      return true;
    }
    return false;
  }

  private Employee sendEmployeeToKafka(Employee employee,
                                       EmployeeOperation operation) {
    EmployeeAvroModel createdEmployeeAvroModel = avroTransformer.getAvroModelFromEmployee(employee, operation);
    employeeKafkaProducer.send(kafkaConfigData.getTopicName(),
        createdEmployeeAvroModel.getUuid(), createdEmployeeAvroModel);
    return employee;
  }

}
