package com.blizzard.test;

/**
 * This is the basic interface that "simple servlets" are provided,
 * providing access to the request data and an interface to control
 * the server response.
 */
public interface SimpleServlet {
  public void doGet(SimpleServletRequest request, SimpleServletResponse response);
}
