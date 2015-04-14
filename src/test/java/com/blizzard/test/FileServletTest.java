package com.blizzard.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Before;
import org.junit.Test;

import mockit.*;

public class FileServletTest {
  @Injectable private SimpleServletRequest request;
  @Injectable private SimpleServletResponse response;
  @Mocked java.io.File file;
  @Mocked java.nio.file.Files files;
  @Mocked java.net.URLConnection urlConnection;
  private FileServlet fileServlet;
  private OutputStream outputStream;

  @Before
  public void setUp() throws Exception {
    fileServlet = new FileServlet();
    outputStream = new ByteArrayOutputStream();
  }

  @Test
  public void sendsProperResponse() throws IOException {
    new Expectations() {{
      request.getUrlPath(); result = "BarPath.css";
      response.getOutputStream(); result = outputStream;
      new File(".", "BarPath.css"); result = file;
      file.toPath();
      Files.copy((java.nio.file.Path) any, outputStream);
      response.setMimeType("text/css");
      response.setStatusCode(200);
    }};

    fileServlet.doGet(request, response);
  }

  @Test
  public void setsStatusTo404OnFileNotFound() throws IOException {
    new Expectations() {{
      request.getUrlPath(); result = "BarPath.css";
      new File(".", "BarPath.css"); result = new java.nio.file.NoSuchFileException("File Not Found");
      response.setStatusCode(404);
    }};

    fileServlet.doGet(request, response);
  }

  @Test
  public void setsStatusTo500OnIOException() throws IOException {
    new Expectations() {{
      request.getUrlPath(); result = "BarPath.css";
      response.getOutputStream(); result = outputStream;
      new File(".", "BarPath.css"); result = file;
      file.toPath();
      Files.copy((java.nio.file.Path) any, outputStream); result = new IOException();
      response.setStatusCode(500);
    }};

    fileServlet.doGet(request, response);
  }
}

