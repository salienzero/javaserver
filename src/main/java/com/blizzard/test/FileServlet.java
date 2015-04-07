package com.blizzard.test;

/**
 * This servlet handles requests for local files
 */
public class FileServlet implements SimpleServlet {
  public FileServlet() {}

  public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
    response.setStatusCode(200);
  }
}
