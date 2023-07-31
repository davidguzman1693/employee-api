package com.coding.challenge.exception;

/**
 * Exception associated to the validation when creating/updating.
 *
 * @author dguzman.
 */
public class ValidationEmployeeException extends EmployeeException {

  public ValidationEmployeeException() {
  }

  public ValidationEmployeeException(String message) {
    super(message);
  }

  public ValidationEmployeeException(String message, Throwable cause) {
    super(message, cause);
  }
}
