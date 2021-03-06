package com.blizzard.test;

import java.io.PrintStream;

/**
 * This servlet just responds back with some of the request's info
 */
public class EchoServlet implements SimpleServlet {
  public EchoServlet() {}

  public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
    response.setMimeType("text/plain");

    PrintStream pstream = new PrintStream(response.getOutputStream());

    pstream.println("Request Host: " + request.getHost());
    pstream.println("Request Path: " + request.getUrlPath());
    String ipAddress = request.getClientAddress().getHostAddress();
    if (request.getClientAddress().isLoopbackAddress() || request.getClientAddress().isAnyLocalAddress()) {
      ipAddress = "127.0.0.1";
    }
    pstream.println("Client IP: " + ipAddress);
    if (request.getHeader("User-Agent") != null) {
      pstream.println("User Agent: " + request.getHeader("User-Agent"));
    }
    if (request.getUrlParameter("echo") != null) {
      pstream.println("Echo Param: " + request.getUrlParameter("echo"));
    }
    pstream.close();

    response.setStatusCode(200);
  }
}
