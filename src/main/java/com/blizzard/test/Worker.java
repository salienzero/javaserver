package com.blizzard.test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

public class Worker implements Runnable {
  private Socket socket;

  public Worker(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    try {
      String requestLine;
      List<String> headerLines = new ArrayList<String>();

      BufferedReader inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      requestLine = inputReader.readLine();

      String headerLine;
      while ( !(headerLine=inputReader.readLine()).equals("") ){
        headerLines.add(headerLine);
      }

      OutputStream outputStream = socket.getOutputStream();
      HTTPResponse response = new HTTPResponse(socket.getOutputStream());
      try {
        HTTPRequest request = new HTTPRequest(requestLine, headerLines, socket.getInetAddress());
        EchoServlet echoServlet = new EchoServlet();
        echoServlet.doGet(request, response);
      } catch (HTTPResponseException e) {
        response.setStatusCode(e.getStatusCode());
        System.out.println(e.getMessage());
      }

      outputStream.close();
      inputReader.close();
      socket.close();
    }
    catch (IOException e) {
      System.out.println(e);
    }
  }
}
