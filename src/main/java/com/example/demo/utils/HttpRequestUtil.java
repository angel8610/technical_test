package com.example.demo.utils;

import java.net.URI;
import java.net.http.HttpRequest;

public final class HttpRequestUtil {

  private HttpRequestUtil() {
  }

  public static HttpRequest.Builder buildRequest(String url) {
    return HttpRequest.newBuilder()
      .uri(URI.create(url));
  }


}
