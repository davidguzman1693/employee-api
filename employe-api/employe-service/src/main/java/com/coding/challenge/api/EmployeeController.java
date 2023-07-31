package com.coding.challenge.api;

import com.coding.challenge.exception.EmployeeException;
import com.coding.challenge.exception.NotFoundEmployeeException;
import com.coding.challenge.exception.ValidationEmployeeException;
import com.coding.challenge.model.Employee;
import com.coding.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@RestController
@RequestMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeeController {

  private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

  private final EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @PostMapping
  public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    LOG.info("Creating employee {}", employee);
    Employee createdEmployee = employeeService.create(employee);
    return ResponseEntity.ok(createdEmployee);
  }

  @PutMapping
  public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee) {
    LOG.info("Updating employee {}", employee);

    Employee employeeUpdated = employeeService.update(employee);
    return ResponseEntity.ok(employeeUpdated);
  }

  @GetMapping(value = "/{idEmployee}")
  public ResponseEntity<Employee> retrieveEmployee(@PathVariable("idEmployee") String idEmployee) {
    LOG.info("Retrieving the employee with id {}", idEmployee);

    Employee employee = employeeService.getById(idEmployee);

    return ResponseEntity.ok(employee);
  }

  @GetMapping
  public ResponseEntity<List<Employee>> retrieveEmployeeList() {
    LOG.info("Retrieving all employees");
    List<Employee> listEmployee = employeeService.getListEmployee();
    return ResponseEntity.ok(listEmployee);
  }

  @DeleteMapping
  public ResponseEntity<Void> deleteEmployee(@RequestBody Employee employee) {
    LOG.info("Deleting the employee {}", employee);

    employeeService.delete(employee);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @ExceptionHandler({EmployeeException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleEmployeException(EmployeeException exception) {
    LOG.warn("An exception occurred {}", exception.getMessage(), exception);
  }

  @ExceptionHandler(ValidationEmployeeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handleValidationEmployeeException(WebRequest request, ValidationEmployeeException exception) {
    LOG.warn("A validation error occurred when creting/updating the  {}", exception.getMessage(), exception);
  }

  @ExceptionHandler(NotFoundEmployeeException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public void handleNotFoundEmployeeException(NotFoundEmployeeException exception) {
    LOG.warn("The requested employee was not found {}", exception.getMessage(), exception);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleUnknownException(RuntimeException exception) {
    LOG.warn("There was an unknown exception: {}", exception.getMessage(), exception);
  }
}
