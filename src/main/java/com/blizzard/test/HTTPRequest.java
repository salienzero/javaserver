package com.blizzard.test;

import java.net.InetAddress;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class HTTPRequest implements SimpleServletRequest {
  private String method;
  private URI uri;
  private Map<String, String> headers;
  private Map<String, String> params;
  private java.net.InetAddress clientAddress;

  public HTTPRequest(InputStream inputStream, java.net.InetAddress clientAddress) throws IOException, HTTPResponseException {
    BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));

    String requestLine = inputReader.readLine();

    String[] requestLineParts = requestLine.split(" ");
    if (requestLineParts.length < 2) {
      throw new HTTPResponseException(400, "BAD REQUEST: Request line not formatted correctly. Example: GET /foo/bar.html");
    }
    method = requestLineParts[0];
    try {
      uri = new URI(requestLineParts[1]);
    } catch (URISyntaxException e) {
      throw new HTTPResponseException(400, "BAD REQUEST: Invalid URI. Example: GET /foo/bar.html", e);
    }

    headers = new HashMap<>();

    String headerLine;
    while ( !(headerLine=inputReader.readLine()).equals("") ){
      int nameValueDelimiterIndex = headerLine.indexOf(":");
      if (nameValueDelimiterIndex < 0) {
        throw new HTTPResponseException(400, "BAD REQUEST: Header line not formatted correctly. Example: foo: bar");
      }
      headers.put(headerLine.substring(0, nameValueDelimiterIndex).trim().toLowerCase(), headerLine.substring(nameValueDelimiterIndex + 1).trim());
    }

    params = new HashMap<>();
    String query = uri.getRawQuery();
    if (query != null) {
      for (String queryPair : query.split("&")) {
        int queryPairDelimiterIndex = queryPair.indexOf("=");
        if (queryPairDelimiterIndex >= 0) {
          params.put(decodeURLPart(queryPair.substring(0, queryPairDelimiterIndex)), decodeURLPart(queryPair.substring(queryPairDelimiterIndex + 1)));
        } else {
          params.put(decodeURLPart(queryPair), "");
        }
      }
    }

    this.clientAddress = clientAddress;
  }

  /**
   * Gets the Host parameter as supplied by the client
   *
   * @return The host
   */
  public String getHost() {
    return headers.get("host");
  }

  /**
   * Gets the entire url path as supplied by the client, including request parameters
   *
   * @return The URL path
   */
  public String getUrlPath() {
    return uri.getPath();
  }

  /**
   * Gets a specific parameter as parsed from request url
   *
   * @param name The parameter name
   * @return The parameter value
   */
  public String getUrlParameter(String name) {
    return params.get(name);
  }

  /**
   * Gets a request header as supplied by the client
   *
   * @param name The header name
   * @return The header value
   */
  public String getHeader(String name) {
    return headers.get(name.toLowerCase());
  }

  /**
   * Gets the client's network address
   *
   * @return The clients network address
   */
  public java.net.InetAddress getClientAddress() {
    return clientAddress;
  }

  private String decodeURLPart(String urlPart) throws HTTPResponseException {
    try {
      return URLDecoder.decode(urlPart, "UTF8");
    } catch (UnsupportedEncodingException e) {
      throw new HTTPResponseException(500, "INTERNAL SERVER ERROR: Unsupported encoding", e);
    }
  }
}