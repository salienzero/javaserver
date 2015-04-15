package com.blizzard.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import mockit.*;

public class HTTPRequestTest {
  @Injectable java.net.InetAddress inetAddress;
  private HTTPRequest request;
  private InputStream inputStream;

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void constructorParsesProperRequest() throws IOException, HTTPResponseException {
    String httpGetRequest = "GET /foo?bar&baz=1&qux=hello%20world HTTP/1.1\n" +
      "User-Agent: curl\n" +
      "Host: localhost:8000\n\n";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());

    request = new HTTPRequest(inputStream, inetAddress);

    assertEquals("localhost:8000", request.getHost());
    assertEquals("/foo", request.getUrlPath());
    assertEquals("", request.getUrlParameter("bar"));
    assertEquals("1", request.getUrlParameter("baz"));
    assertEquals("hello world", request.getUrlParameter("qux"));
    assertEquals("curl", request.getHeader("User-Agent"));
    assertEquals(inetAddress, request.getClientAddress());
  }

  @Test
  public void constructorThrows400OnBadRequestLine() throws IOException {
    String httpGetRequest = "GETBAD\n" +
      "User-Agent: curl\n" +
      "Host: localhost:8000\n\n";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());

    boolean expectedExceptionThrown = false;

    try {
      request = new HTTPRequest(inputStream, inetAddress);
    } catch (HTTPResponseException e) {
      assertEquals(400, e.getStatusCode());
      expectedExceptionThrown = true;
    }

    assertTrue(expectedExceptionThrown);
  }

  @Test
  public void constructorThrows400OnEmptyRequest() throws IOException {
    String httpGetRequest = "";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());

    boolean expectedExceptionThrown = false;

    try {
      request = new HTTPRequest(inputStream, inetAddress);
    } catch (HTTPResponseException e) {
      assertEquals(400, e.getStatusCode());
      expectedExceptionThrown = true;
    }

    assertTrue(expectedExceptionThrown);
  }

  @Test
  public void constructorThrows400OnBadURL(@Mocked java.net.URI uri) throws IOException, java.net.URISyntaxException {
    String httpGetRequest = "GET BADURI\n" +
      "User-Agent: curl\n" +
      "Host: localhost:8000\n\n";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());

    new Expectations() {{
      new java.net.URI("BADURI"); result = new java.net.URISyntaxException("foo", "bar"); 
    }};

    boolean expectedExceptionThrown = false;

    try {
      request = new HTTPRequest(inputStream, inetAddress);
    } catch (HTTPResponseException e) {
      assertEquals(400, e.getStatusCode());
      expectedExceptionThrown = true;
    }

    assertTrue(expectedExceptionThrown);
  }

  @Test
  public void constructorThrows400OnBadHeader() throws IOException {
    String httpGetRequest = "GET /foo?bar=100\n" +
      "User-Agent: curl\n" +
      "BADHEADER\n" +
      "Host: localhost:8000\n\n";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());

    boolean expectedExceptionThrown = false;

    try {
      request = new HTTPRequest(inputStream, inetAddress);
    } catch (HTTPResponseException e) {
      assertEquals(400, e.getStatusCode());
      expectedExceptionThrown = true;
    }

    assertTrue(expectedExceptionThrown);
  }

  @Test
  public void constructorThrows500OnUnsupportedEncoding(@Mocked java.net.URLDecoder urlDecoder) throws IOException {
    String httpGetRequest = "GET /foo?bar=100\n" +
      "User-Agent: curl\n" +
      "Host: localhost:8000\n\n";

    inputStream = new ByteArrayInputStream(httpGetRequest.getBytes());

    new Expectations() {{
      java.net.URLDecoder.decode(anyString, anyString); result = new java.io.UnsupportedEncodingException();
    }};

    boolean expectedExceptionThrown = false;

    try {
      request = new HTTPRequest(inputStream, inetAddress);
    } catch (HTTPResponseException e) {
      assertEquals(500, e.getStatusCode());
      expectedExceptionThrown = true;
    }

    assertTrue(expectedExceptionThrown);
  }
}
