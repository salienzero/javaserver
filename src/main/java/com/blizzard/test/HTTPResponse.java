package com.blizzard.test;

import java.util.Map;
import java.util.HashMap;

public class HTTPResponse implements SimpleServletResponse {
  private int statusCode;
  private String mimeType;
  private Map<String, String> headers;
  private java.io.OutputStream outputStream;

  public HTTPResponse(java.io.OutputStream outputStream) {
    headers = new HashMap<String,String>();
    this.outputStream = outputStream;
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
    this.mimeType = mimeType;
  }

  /**
   * Gets the OutputStream that when written to will transmit to the client.
   *
   * @return The output stream
   */
  public java.io.OutputStream getOutputStream() {
    return outputStream;
  }
}
