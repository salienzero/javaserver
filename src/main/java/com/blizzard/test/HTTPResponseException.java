package com.blizzard.test;

public class HTTPResponseException extends Exception {
  private int statusCode;

  public HTTPResponseException(int statusCode, String message) {
      super(message);
      this.statusCode = statusCode;
  }

  public HTTPResponseException(int statusCode, String message, Exception e) {
      super(message, e);
      this.statusCode = statusCode;
  }

  public int getStatusCode() {
      return statusCode;
  }
}
