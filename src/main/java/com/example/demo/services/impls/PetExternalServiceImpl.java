package com.example.demo.services.impls;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.services.PetExternalService;
import com.example.demo.vos.PetVO;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
public class PetExternalServiceImpl implements PetExternalService {

  private final RestClient restClient;

  public PetExternalServiceImpl(RestClient restClient) {
    this.restClient = restClient;
  }

  @Override
  @Retry(name = "demoApiRetry", fallbackMethod = "connectTimeout")
  public Optional<PetVO> findById(Long petId) {
    PetVO petVO = this.restClient.get()
      .uri("/{petId}", petId)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .onStatus(status -> status == HttpStatus.NOT_FOUND, ((request, response) -> {
        throw new PetNotFoundException("Not Fund pet with ID: ".concat(petId.toString()));
      }))
      .onStatus(HttpStatusCode::isError, (request, response) -> {
        throw new PetException("External server error");
      })
      .body(PetVO.class);
    return Optional.ofNullable(petVO);
  }

  @Override
  @Retry(name = "demoApiRetry", fallbackMethod = "connectTimeout")
  public PetVO save(PetVO petVO) {
    return this.restClient.post()
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .body(petVO)
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
        throw new PetException("External validation error");
      }))
      .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
        throw new PetException("Temporary external server error");
      }))
      .body(PetVO.class);
  }
  private void connectTimeout(Exception ex) {
    System.out.println("Error con: "+ex.getMessage());
    throw new PetException("External server error");
  }


}
