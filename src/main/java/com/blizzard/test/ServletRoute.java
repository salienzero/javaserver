package com.blizzard.test;

/**
 * This represents a route mapping for a servlet
 * Currently, very simplistic (only supports a string used for start-of-path matching)
 */
public class ServletRoute {
  private String route;
  private SimpleServlet servlet;

  public ServletRoute(String route, SimpleServlet servlet) {
    this.route = route;
    this.servlet = servlet;
  }

  public String getRoute() {
    return route;
  }

  public SimpleServlet getServlet() {
    return servlet;
  }
}
