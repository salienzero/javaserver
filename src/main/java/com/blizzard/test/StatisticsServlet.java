package com.blizzard.test;

import java.io.OutputStream;
import java.io.IOException;

/**
 * Adds basic statistics tracking
 */
public class StatisticsServlet {
  private SimpleServlet servlet;
  private int activeRequests;
  private int totalRequests;
  private long averageResponseTime;
  
  public StatisticsServlet(SimpleServlet servlet) {
    this.servlet = servlet;
    totalRequests = 0;
    activeRequests = 0;
    averageResponseTime = 0;
  }

  public void doGetWithStatistics(HTTPRequest request, HTTPResponse response, OutputStream outputStream) throws IOException {
    long start = System.nanoTime();
    incrementActiveRequests();
    try {
      servlet.doGet(request, response);
      response.send(outputStream);
    } finally {
      long finish = System.nanoTime();
      decrementActiveRequests();
      updateStatistics(start, finish);
      printStatistics();
    }
  }

  private synchronized int incrementActiveRequests() {
    return activeRequests++;
  }

  private synchronized int decrementActiveRequests() {
    return activeRequests--;
  }

  private synchronized void updateStatistics(long start, long finish) {
    averageResponseTime = ((averageResponseTime * totalRequests) + (finish-start))/(totalRequests + 1);
    totalRequests++;
  }

  private synchronized void printStatistics() {
    System.out.println("Statistics for: " + servlet.getClass());
    System.out.println("Active requests: " + activeRequests);
    System.out.println("Total requests: " + totalRequests);
    System.out.println("Average response time (milliseconds): " + averageResponseTime/1000000);
  }

  public SimpleServlet getServlet() {
    return servlet;
  }

  public synchronized int getActiveRequests() {
    return activeRequests;
  }

  public synchronized int getTotalRequests() {
    return totalRequests;
  }

  public synchronized long getAverageResponseTime() {
    return averageResponseTime;
  }
}
