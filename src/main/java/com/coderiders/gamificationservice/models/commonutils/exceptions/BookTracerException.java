package com.coderiders.gamificationservice.models.commonutils.exceptions;

public class BookTracerException extends RuntimeException {

  public BookTracerException() {
    super();
  }

  public BookTracerException(String message) {
    super(message);
  }

  public BookTracerException(Throwable cause) {
    super(cause);
  }

  public BookTracerException(String message, Throwable cause) {
    super(message, cause);
  }
}