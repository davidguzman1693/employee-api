package com.coding.challenge.api;

import com.coding.challenge.exception.EmployeeException;
import com.coding.challenge.exception.NotFoundEmployeeException;
import com.coding.challenge.exception.ValidationEmployeeException;
import com.coding.challenge.model.Employee;
import com.coding.challenge.service.EmployeeService;
import com.coding.challenge.swagger.SpringFoxConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
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
@Api(tags = {SpringFoxConfig.EMPLOYEE_TAG}, produces = "Employees", consumes = "Employee information")
public class EmployeeController {

  private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

  private final EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @Operation(summary = "Creates a new employee", description = "Creates a new employee per request")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "When the employee has been created correctly"),
      @ApiResponse(code = 400, message = "When the information from the employee is not valid"),
      @ApiResponse(code = 500, message = "When an internal error happened when creating the employee")
  })
  @PostMapping
  public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    LOG.info("Creating employee {}", employee);
    Employee createdEmployee = employeeService.create(employee);
    return ResponseEntity.ok(createdEmployee);
  }

  @Operation(summary = "Update an employee", description = "Updates an employee per request")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "When the employee has been updated correctly"),
      @ApiResponse(code = 404, message = "When the employee to update has not been found"),
      @ApiResponse(code = 400, message = "When the information to update is not valid"),
      @ApiResponse(code = 500, message = "When an internal error occurred")
  })
  @PutMapping
  public ResponseEntity<Employee> updateEmployee(@RequestBody Employee employee) {
    LOG.info("Updating employee {}", employee);

    Employee employeeUpdated = employeeService.update(employee);
    return ResponseEntity.ok(employeeUpdated);
  }

  @Operation(summary = "Retrieves an employee", description = "Retrieves an employee given an id")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "When the employee has been"),
      @ApiResponse(code = 404, message = "When the employee has not been found"),
      @ApiResponse(code = 500, message = "When an internal error (i.e. database connection found) occurs.")
  })
  @GetMapping(value = "/{idEmployee}")
  public ResponseEntity<Employee> retrieveEmployee(@PathVariable("idEmployee") String idEmployee) {
    LOG.info("Retrieving the employee with id {}", idEmployee);

    Employee employee = employeeService.getById(idEmployee);

    return ResponseEntity.ok(employee);
  }

  @Operation(summary = "Retrieves all employees", description = "Retrieves all employees from database")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "When all employees have been retrieved correctly"),
      @ApiResponse(code = 500, message = "When an internal error happened when creating the employee")
  })
  @GetMapping
  public ResponseEntity<List<Employee>> retrieveEmployeeList() {
    LOG.info("Retrieving all employees");
    List<Employee> listEmployee = employeeService.getListEmployee();
    return ResponseEntity.ok(listEmployee);
  }

  @Operation(summary = "", description = "")
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "When all employee has been deleted correctly."),
      @ApiResponse(code = 400, message = "When information of employee to delete is not correct"),
      @ApiResponse(code = 500, message = "When an internal error happened when deleting the employee")
  })
  @DeleteMapping(value = "/{idEmployee}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable("idEmployee") String idEmployee) {
    LOG.info("Deleting the employee with id {}", idEmployee);

    employeeService.delete(idEmployee);
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
