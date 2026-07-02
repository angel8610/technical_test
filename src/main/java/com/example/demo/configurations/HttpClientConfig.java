package com.example.demo.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class HttpClientConfig {

  private final HttpClientProperties properties;

  public HttpClientConfig(HttpClientProperties properties) {
    this.properties = properties;
  }

  @Bean
  public HttpClient httpClient() {
    return HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_2)
      .connectTimeout(Duration.ofSeconds(15))
      .build();
  }

  @Bean
  public RestClient restClient() {
    return RestClient.builder()
      .baseUrl(properties.getBaseUrl())
      .defaultHeader(HttpHeaders.ACCEPT, "application/json")
      .build();
  }


}
