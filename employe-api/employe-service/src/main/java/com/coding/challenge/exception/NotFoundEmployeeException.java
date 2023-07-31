package com.coding.challenge.exception;

/**
 * Exception thrown when the no employee is found.
 *
 * @author dguzman.
 */
public class NotFoundEmployeeException extends EmployeeException {
  public NotFoundEmployeeException() {
  }

  public NotFoundEmployeeException(String message) {
    super(message);
  }

  public NotFoundEmployeeException(String message, Throwable cause) {
    super(message, cause);
  }
}
