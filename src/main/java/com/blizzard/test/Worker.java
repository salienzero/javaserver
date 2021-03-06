package com.blizzard.test;

import java.io.IOException;
import java.io.Closeable;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class Worker implements Runnable {
  private Socket socket;
  private List<ServletRoute> servletRoutes;

  public Worker(Socket socket, List<ServletRoute> servletRoutes) {
    this.socket = socket;
    this.servletRoutes = servletRoutes;
  }

  public void run() {
    InputStream inputStream = null;
    OutputStream outputStream = null;

    try {
      inputStream = socket.getInputStream();
      outputStream = socket.getOutputStream();
      HTTPResponse response = new HTTPResponse();

      try {
        try {
          HTTPRequest request = new HTTPRequest(inputStream, socket.getInetAddress());
          StatisticsServlet mappedServlet = null;
          for (ServletRoute servletRoute : servletRoutes) {
            if (request.getUrlPath().startsWith(servletRoute.getRoute())) {
              mappedServlet = servletRoute.getServlet();
            }
          }
          if (mappedServlet != null) {
            mappedServlet.doGetWithStatistics(request, response, outputStream);
          } else {
            HTTPResponse.sendException(outputStream, new HTTPResponseException(404, "Not Found"));
          }
        } catch (HTTPResponseException e) {
          System.out.println(e.getMessage());
          HTTPResponse.sendException(outputStream, e);
        }
      } catch (Exception e) {
        // For anything really unexpected, try once to send a 500 error (if the error was in sending to the socket this will of course fail)
        e.printStackTrace(new java.io.PrintStream(System.out));
        HTTPResponse.sendException(outputStream, new HTTPResponseException(500, "Internal Server Error", e));
      }
    }
    catch (IOException e) {
      System.out.println(e);
    } finally {
      forceClose(outputStream);
      forceClose(inputStream);
      forceClose(socket);
    }
  }

  private static void forceClose(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (IOException ignored) {}
    }
  }
}
