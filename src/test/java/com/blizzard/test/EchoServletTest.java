package com.blizzard.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

import mockit.*;

public class EchoServletTest {
  @Injectable private SimpleServletRequest request;
  @Injectable private SimpleServletResponse response;
  private EchoServlet echoServlet;
  private OutputStream outputStream;

  @Before
  public void setUp() throws Exception {
    echoServlet = new EchoServlet();
    outputStream = new ByteArrayOutputStream();
  }

  @Test
  public void sendsProperResponse(@Injectable java.net.InetAddress inetAddress) throws IOException {
    new Expectations() {{
      response.setMimeType("text/plain");
      response.getOutputStream(); result = outputStream;
      request.getClientAddress(); result = inetAddress;
      inetAddress.isAnyLocalAddress(); result = true;
      request.getHost(); result = "FooHost";
      request.getUrlPath(); result = "BarPath";
      request.getHeader("User-Agent"); result = "BazAgent";
      request.getUrlParameter("echo"); result = "QuxEcho";
      response.setStatusCode(200);
    }};

    echoServlet.doGet(request, response);

    String responseBody = outputStream.toString();
    assertThat(responseBody, containsString("Request Host: FooHost\n"));
    assertThat(responseBody, containsString("Request Path: BarPath\n"));
    assertThat(responseBody, containsString("Client IP: 127.0.0.1\n"));
    assertThat(responseBody, containsString("User Agent: BazAgent\n"));
    assertThat(responseBody, containsString("Echo Param: QuxEcho\n"));
  }
}
