package com.coding.challenge.exception;

/**
 * Exception covering any aspect related to the logic of Employee Service.
 *
 * @author dguzman.
 */
public class EmployeeException extends RuntimeException {

  public EmployeeException() {
  }

  public EmployeeException(String message) {
    super(message);
  }

  public EmployeeException(String message, Throwable cause) {
    super(message, cause);
  }
}
