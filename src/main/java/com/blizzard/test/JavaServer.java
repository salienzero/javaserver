package com.blizzard.test;

import java.net.ServerSocket;
import java.net.Socket;

public class JavaServer {
  public static void main(String args[]) throws Exception {
    ServerSocket serverSocket = new ServerSocket(8000);
    System.out.println("Server listening on " + serverSocket.getLocalPort());
    while (true) {
      Socket socket = serverSocket.accept();
      System.out.println("Connected");
      new Thread(new Worker(socket)).start();
    }
  }
}
