package com.duocuc.backend_srv.exception;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
