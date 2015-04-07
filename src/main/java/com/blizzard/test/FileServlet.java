package com.blizzard.test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.io.IOException;
import java.net.URLConnection;

/**
 * This servlet handles requests for local files
 */
public class FileServlet implements SimpleServlet {
  public FileServlet() {}

  public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
    try {
      // Would not normally use relative pathing here, but allow setting root via config or command line arg
      File file = new File(".", request.getUrlPath());
      Files.copy(file.toPath(), response.getOutputStream());
      String mimeType = URLConnection.getFileNameMap().getContentTypeFor(request.getUrlPath());
      if (mimeType == null) {
        // Can't seem to detect css file type with built in utilities; would solve with an external library normally
        if (request.getUrlPath().endsWith(".css")) {
          mimeType = "text/css";
        }
      }
      response.setMimeType(mimeType);
      response.setStatusCode(200);
    } catch (NoSuchFileException e) {
      response.setStatusCode(404);
    } catch (IOException e) {
      System.out.println(e);
      response.setStatusCode(500);
    }
  }
}
