package com.blizzard.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

import mockit.*;

public class HTTPResponseTest {
  private HTTPResponse response;
  private OutputStream outputStream;

  @Before
  public void setUp() throws Exception {
    outputStream = new ByteArrayOutputStream();
  }

  @Test
  public void sendsProperResponse() throws IOException {
    response = new HTTPResponse();
    response.setStatusCode(200);
    response.setHeader("foo", "bar");
    response.setHeader("baz", "qux");
    response.setMimeType("text/plain");
    PrintStream printStream = new PrintStream(response.getOutputStream());
    printStream.println("Hello");
    printStream.println("World");
    
    response.send(outputStream);
    String response = outputStream.toString();
    assertThat(response, startsWith("HTTP/1.1 200 OK"));
    assertThat(response, containsString("Date:"));
    assertThat(response, containsString("Content-Length: 12\n"));
    assertThat(response, containsString("foo: bar\n"));
    assertThat(response, containsString("baz: qux\n"));
    assertThat(response, containsString("Content-Type: text/plain\n"));
    assertThat(response, endsWith("\n\nHello\nWorld\n"));
  }

  @Test
  public void sendsProperExceptionResponse(@Mocked HTTPResponseException responseException) throws IOException {
    new Expectations() {{
      responseException.getStatusCode(); result = 500;
      responseException.getMessage(); result = "Error Message Foo";
    }};

    HTTPResponse.sendException(outputStream, responseException);

    String response = outputStream.toString();
    assertThat(response, startsWith("HTTP/1.1 500 Internal Server Error"));
    assertThat(response, containsString("Date:"));
    assertThat(response, containsString("Content-Length: 18\n"));
    assertThat(response, containsString("Content-Type: text/plain\n"));
    assertThat(response, endsWith("\n\nError Message Foo\n"));
  }

  @Test
  public void sendsProperResponseWithIncompleteInfo() throws IOException {
    response = new HTTPResponse();
    response.setStatusCode(404);
    
    response.send(outputStream);
    String response = outputStream.toString();
    assertThat(response, startsWith("HTTP/1.1 404 Not Found"));
    assertThat(response, containsString("Date:"));
    assertThat(response, containsString("Content-Length: 10\n"));
    assertThat(response, containsString("Content-Type: text/plain\n"));
    assertThat(response, endsWith("\n\nNot Found\n"));
  }
}
