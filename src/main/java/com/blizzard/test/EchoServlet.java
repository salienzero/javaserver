package com.blizzard.test;

import java.io.PrintStream;

/**
 * This is the basic interface that "simple servlets" are provided,
 * providing access to the request data and an interface to control
 * the server response.
 */
public class EchoServlet implements SimpleServlet {
  public EchoServlet() {}

  public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
    PrintStream pstream = new PrintStream(response.getOutputStream());

    pstream.println("Request Host: " + request.getHost());
    pstream.close();
  }
}
