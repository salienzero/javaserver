package com.blizzard.test;

import static org.junit.Assert.*;
import org.junit.Test;

import mockit.*;

public class ServletRouteTest {
  @Injectable StatisticsServlet statisticsServlet;
  ServletRoute servletRoute;

  @Test
  public void constructorSetsProperties() {
    servletRoute = new ServletRoute("foo/bar", statisticsServlet);

    assertEquals("foo/bar", servletRoute.getRoute());
    assertEquals(statisticsServlet, servletRoute.getServlet());
  }
}
