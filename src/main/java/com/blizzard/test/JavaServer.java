package com.blizzard.test;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class JavaServer {
  public static void main(String args[]) throws Exception {
    List<ServletRoute> servletRoutes = new ArrayList<>();
    servletRoutes.add(new ServletRoute("/echo", new StatisticsServlet(new EchoServlet())));
    servletRoutes.add(new ServletRoute("/files", new StatisticsServlet(new FileServlet())));

    ServerSocket serverSocket = new ServerSocket(8000);
    System.out.println("Server listening on " + serverSocket.getLocalPort());
    while (true) {
      Socket socket = serverSocket.accept();
      System.out.println("Connected");
      new Thread(new Worker(socket, servletRoutes)).start();
    }
  }
}
