package com.blizzard.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import mockit.*;
import static mockit.Deencapsulation.*;

public class StatisticsServletTest {
  @Injectable private SimpleServlet servlet;
  @Mocked private HTTPRequest request;
  @Mocked private HTTPResponse response;
  private StatisticsServlet statisticsServlet;
  private OutputStream outputStream;

  @Before
  public void setUp() throws Exception {
    statisticsServlet = new StatisticsServlet(servlet);
    outputStream = new ByteArrayOutputStream();
  }

  @Test
  public void initializesCorrectly() {
    assertEquals(servlet, statisticsServlet.getServlet());
    assertEquals(0, statisticsServlet.getActiveRequests());
    assertEquals(0, statisticsServlet.getTotalRequests());
    assertEquals(0, statisticsServlet.getAverageResponseTime());
  }

  @Test
  public void doGetWithStatisticsInvokesGetAndSendsResponse() throws IOException{
    new Expectations() {{
      servlet.doGet(request, response);
      response.send(outputStream);
    }};

    statisticsServlet.doGetWithStatistics(request, response, outputStream);
  }

  @Test
  public void doGetWithStatisticsUpdatesStatistics(@Mocked("nanoTime") final System system) throws IOException{
    setField(statisticsServlet, "activeRequests", 5);
    setField(statisticsServlet, "totalRequests", 99);
    setField(statisticsServlet, "averageResponseTime", 100000000L);

    new StrictExpectations() {{
      system.nanoTime(); returns(100000000L);
      system.nanoTime(); returns(300000000L);
    }};

    statisticsServlet.doGetWithStatistics(request, response, outputStream);

    assertEquals(5, statisticsServlet.getActiveRequests());
    assertEquals(100, statisticsServlet.getTotalRequests());
    assertEquals(101000000L, statisticsServlet.getAverageResponseTime());
  }

  @Test
  public void doGetWithStatisticsUpdatesStatisticsWhenGetThrows(@Mocked("nanoTime") final System system) throws IOException{
    setField(statisticsServlet, "activeRequests", 3);
    setField(statisticsServlet, "totalRequests", 99);
    setField(statisticsServlet, "averageResponseTime", 100000000L);

    new StrictExpectations() {{
      system.nanoTime(); returns(100000000L);
      servlet.doGet((HTTPRequest) any, (HTTPResponse) any); result = new Exception();
      system.nanoTime(); returns(500000000L);
    }};

    try {
      statisticsServlet.doGetWithStatistics(request, response, outputStream);
    } catch (Exception ignored) {}

    assertEquals(3, statisticsServlet.getActiveRequests());
    assertEquals(100, statisticsServlet.getTotalRequests());
    assertEquals(103000000L, statisticsServlet.getAverageResponseTime());
  }
}
