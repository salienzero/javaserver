package com.blizzard.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import mockit.*;

public class JavaServerTest {
  @Mocked ServerSocket serverSocket;
  @Injectable Socket socket;
  @Mocked Worker worker;
  @Mocked Thread thread;

  @Before
  public void setUp() throws Exception {
    new NonStrictExpectations() {{
      // Ensure we never actually enter an infinite loop, or block on awaiting a connection
      serverSocket.accept(); result = socket;
      thread.start(); result = new IOException();
    }};
  }

  @Test
  public void sendsProperResponse() throws Exception {
    new Expectations() {{
      new ServerSocket(8000);
      serverSocket.getLocalPort(); result = 8000;
      new Worker(socket, (List<ServletRoute>) any);
      new Thread((Worker) any);
    }};

    try {
      JavaServer.main(new String[0]);
    } catch (IOException expected) {}
  }
}
