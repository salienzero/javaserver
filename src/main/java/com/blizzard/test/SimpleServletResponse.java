package com.blizzard.test;

/**
 * This interface encapsulates the HTTP response portion of the
 * SimpleServlet specification.
 *
 */
interface SimpleServletResponse {
  /**
   * Set the response HTTP code
   *
   * @param httpStatusCode
   */
  public void setStatusCode(int httpStatusCode);

  /**
   * Sets the response header to specified value
   *
   * @param name The parameter name
   * @param value The parameter value
   */
  public void setHeader(String name, String value);

  /**
   * Sets the mime type to be returned to the client
   *
   * @param mimeType The mime type to set
   */
  public void setMimeType(String mimeType);

  /**
   * Gets the OutputStream that when written to will transmit to the client.
   *
   * @return The output stream
   */
  public java.io.OutputStream getOutputStream();
}
