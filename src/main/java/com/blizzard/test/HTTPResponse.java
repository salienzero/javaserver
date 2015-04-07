package com.blizzard.test;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.io.PrintStream;

public class HTTPResponse implements SimpleServletResponse {
  private static final Map<Integer, String> statusCodesMap;
  static {
    Map<Integer, String> initStatusCodesMap = new HashMap<>();
    initStatusCodesMap.put(200, "OK");
    initStatusCodesMap.put(400, "Bad Request");
    initStatusCodesMap.put(404, "Not Found");
    initStatusCodesMap.put(500, "Internal Server Error");
    statusCodesMap = Collections.unmodifiableMap(initStatusCodesMap);
  }

  private int statusCode;
  private Map<String, String> headers;
  private boolean headerSet;
  private java.io.ByteArrayOutputStream outputStream;

  public HTTPResponse() {
    headers = new HashMap<>();
    this.outputStream = new java.io.ByteArrayOutputStream();
    this.headerSet = false;
  }

  private HTTPResponse(HTTPResponseException httpResponseException) {
    this();
    statusCode = httpResponseException.getStatusCode();
    setMimeType("text/plain");
  }

  /**
   * Set the response HTTP code
   *
   * @param httpStatusCode
   */
  public void setStatusCode(int httpStatusCode) {
    statusCode = httpStatusCode;
  }

  /**
   * Sets the response header to specified value
   *
   * @param name The parameter name
   * @param value The parameter value
   */
  public void setHeader(String name, String value) {
    headers.put(name, value);
  }

  /**
   * Sets the mime type to be returned to the client
   *
   * @param mimeType The mime type to set
   */
  public void setMimeType(String mimeType) {
    headers.put("Content-Type", mimeType);
  }

  /**
   * Gets the OutputStream that when written to will transmit to the client.
   *
   * @return The output stream
   */
  public java.io.OutputStream getOutputStream() {
    return outputStream;
  }

  public static void sendException(java.io.OutputStream destinationStream, HTTPResponseException httpResponseException) throws java.io.IOException {
    HTTPResponse response = new HTTPResponse(httpResponseException);
    PrintStream pstream = new PrintStream(response.getOutputStream());
    pstream.println(httpResponseException.getMessage());
    response.send(destinationStream);
  }

  public void send(java.io.OutputStream destinationStream) throws java.io.IOException {
    PrintStream pstream = new PrintStream(destinationStream);

    pstream.println("HTTP/1.1 " + statusCode + " " + statusCodesMap.get(statusCode));

    if (headers.get("Date") == null) {
      java.time.ZonedDateTime now = java.time.ZonedDateTime.now();
      headers.put("Date", java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(now));
    }

    if (headers.get("Content-Type") == null) {
      headers.put("Content-Type", "text/plain");
    }

    // Set a default body for failures with no body
    if (statusCode != 200 && outputStream.size() == 0) {
      PrintStream printOutputStream = new PrintStream(outputStream);
      printOutputStream.println(statusCodesMap.get(statusCode));
    }

    headers.put("Content-Length", Integer.toString(outputStream.size()));

    for (String headerKey : headers.keySet()) {
      pstream.println(headerKey + ": " + headers.get(headerKey));
    }

    pstream.println();

    outputStream.writeTo(pstream);
  }
}
