package com.coding.challenge.service;

import com.coding.challenge.exception.EmployeeException;
import com.coding.challenge.model.Employee;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Employee service for attending requests for CRUD on Employee.
 *
 * @author dguzman.
 */
public interface EmployeeService {

  /**
   * Creates an employee.
   *
   * @param employee {@link Employee}.
   * @return an {@link Employee} in case everything is good. Otherwise, {@link EmployeeException}.
   */
  Employee create(@Nonnull Employee employee);

  /**
   * Updates an {@link Employee}.
   *
   * @param employee {@link Employee}.
   * @return an {@link Employee} updated. Otherwise, {@link EmployeeException}.
   */
  @Nullable
  Employee update(@Nonnull Employee employee);

  /**
   * Gets an {@link Employee} by Id-
   *
   * @param id to identify the employee.
   * @return an {@link Employee} if found, otherwise {@link EmployeeException}.
   */
  @Nullable
  Employee getById(@Nonnull String id);

  /**
   * Gets the list of {@link Employee}.
   *
   * @return a list of {@link Employee}.
   */
  @Nonnull
  List<Employee> getListEmployee();

  /**
   * Deletes an {@link Employee}.
   *
   * @param id of the {@link Employee}.
   */
  @Nonnull
  void delete(@Nonnull String id);
}
