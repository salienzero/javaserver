package com.blizzard.test;

/**
 * This represents a route mapping for a servlet
 * Currently, very simplistic (only supports a string used for start-of-path matching)
 */
public class ServletRoute {
  private String route;
  private StatisticsServlet servlet;

  public ServletRoute(String route, StatisticsServlet servlet) {
    this.route = route;
    this.servlet = servlet;
  }

  public String getRoute() {
    return route;
  }

  public StatisticsServlet getServlet() {
    return servlet;
  }
}
