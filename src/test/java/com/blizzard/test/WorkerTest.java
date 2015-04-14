package com.blizzard.test;

import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import mockit.*;

public class WorkerTest {
  @Mocked private Socket socket;
  @Injectable private StatisticsServlet servlet;
  private List<ServletRoute> servletRoutes;
  private InputStream inputStream;
  private OutputStream outputStream;

  @Before
  public void setUp() throws Exception {
    servletRoutes = new ArrayList<>();
    servletRoutes.add(new ServletRoute("/foo", servlet));

    String httpGetRequest = "GET / HTTP/1.1\n" +
      "User-Agent: curl\n" +
      "Host: localhost:8000\n\n";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());
    outputStream = new ByteArrayOutputStream();

    new NonStrictExpectations() {{
      socket.getInputStream(); result = inputStream;
      socket.getOutputStream(); result = outputStream;
      socket.getInetAddress();
    }};
  }

  @Test
  public void runGetsSocketStreamsAndIPAndClosesSocket() throws Exception {
    new Expectations() {{
      socket.getInputStream(); result = inputStream;
      socket.getOutputStream(); result = outputStream;
      socket.getInetAddress();
      socket.close(); times = 1;
    }};

    Worker worker = new Worker(socket, servletRoutes);
    worker.run();
  }

  @Test
  public void runConstructsRequest(@Mocked HTTPRequest request, @Injectable java.net.InetAddress inetAddress) throws Exception {
    new Expectations() {{
      new HTTPRequest(inputStream, inetAddress);
      request.getUrlPath(); result = "/bar";
      socket.getInetAddress(); result = inetAddress;
    }};

    Worker worker = new Worker(socket, servletRoutes);
    worker.run();
  }

  @Test
  public void runSends404IfNoRouteMatches(@Mocked HTTPResponse response, @Mocked HTTPResponseException exception) throws Exception {
    new Expectations() {{
      new HTTPResponseException(404, "Not Found"); result = exception;
      HTTPResponse.sendException(outputStream, exception);
    }};

    Worker worker = new Worker(socket, servletRoutes);
    worker.run();
  }

  @Test
  public void runSendsHTTPResponseExceptionWhenThrown(@Mocked HTTPRequest request, @Mocked HTTPResponse response) throws Exception {
    HTTPResponseException exception = new HTTPResponseException(400, "Bad Request");
    new Expectations() {{
      new HTTPRequest((InputStream) any, (java.net.InetAddress) any); result = exception;
      HTTPResponse.sendException(outputStream, exception);
    }};

    Worker worker = new Worker(socket, servletRoutes);
    worker.run();
  }

  @Test
  public void runSends500OnUnexpectedException(@Mocked HTTPRequest request, @Mocked HTTPResponse response, @Mocked HTTPResponseException exception) throws Exception {
    Exception unexpectedException = new Exception();
    new Expectations() {{
      new HTTPResponseException(500, "Internal Server Error", unexpectedException); result = exception;
      new HTTPRequest((InputStream) any, (java.net.InetAddress) any); result = unexpectedException;
      HTTPResponse.sendException(outputStream, exception);
    }};

    Worker worker = new Worker(socket, servletRoutes);
    worker.run();
  }

  @Test
  public void runSendsNothingOnSocketIOException(@Mocked HTTPResponse response) throws Exception {
    IOException ioException = new IOException();
    new Expectations() {{
      socket.getInputStream(); result = ioException;
    }};

    Worker worker = new Worker(socket, servletRoutes);
    worker.run();

    new FullVerifications(response) {};
  }

  @Test
  public void runFindsMatchingServletAndInvokesGetWithStatistics(@Injectable final StatisticsServlet correctServlet) throws Exception {
    servletRoutes.add(new ServletRoute("/find_me", correctServlet));

    String httpGetRequest = "GET /find_me/bar/baz HTTP/1.1\n" +
      "User-Agent: curl\n" +
      "Host: localhost:8000\n\n";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());
    new Expectations() {{
      socket.getInputStream(); result = inputStream;
      correctServlet.doGetWithStatistics((HTTPRequest) any, (HTTPResponse) any, outputStream);
    }};

    Worker worker = new Worker(socket, servletRoutes);
    worker.run();

    new FullVerifications(servlet) {};
  }
}
