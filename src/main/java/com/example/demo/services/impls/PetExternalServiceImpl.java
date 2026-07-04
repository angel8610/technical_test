package com.example.demo.services.impls;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.services.PetExternalService;
import com.example.demo.vos.PetVO;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.rmi.RemoteException;
import java.time.Duration;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PetExternalServiceImpl implements PetExternalService {

  private static final Logger LOGGER = Logger.getLogger(PetExternalServiceImpl.class.getName());

  private static final String UNAVAILABLE_MESSAGE = "External service is unavailable. Please try again later.";

  private final RestClient restClient;

  private final Retry retry;

  public PetExternalServiceImpl(RestClient restClient, RetryRegistry retryRegistry) {
    this.restClient = restClient;
    RetryConfig retryConfig = RetryConfig.custom()
      .maxAttempts(3)
      .waitDuration(Duration.ofMillis(200))
      .retryExceptions(ResourceAccessException.class, PetException.class)
      .build();
    this.retry = retryRegistry.retry("demoApi", retryConfig);
  }

  @Override
  public Optional<PetVO> findById(Long petId) {
    PetVO petVO = null;
    final Retry.Context<PetVO> context = retry.context();
    boolean stopped = false;

    do {
      try {
        LOGGER.info("Trying to retrieve search");
        petVO = this.restClient.get()
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

        assert petVO != null;
        stopped = !context.onResult(petVO);
      } catch(ResourceAccessException ex) {
        this.managerContextThrowException(context, ex);
      }
    } while(!stopped);
    context.onComplete();
    LOGGER.info("Successfully search");

    return Optional.ofNullable(petVO);
  }

  @Override
  public PetVO save(PetVO petVO) {
    PetVO petVOSave = null;
    final Retry.Context<PetVO> context = retry.context();
    boolean stopped = false;

    do {
      try {
        LOGGER.info("Trying to retrieve save");
        petVOSave = this.restClient.post()
          .contentType(MediaType.APPLICATION_JSON)
          .accept(MediaType.APPLICATION_JSON)
          .body(petVO)
          .retrieve()
          .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
            throw new RuntimeException("External validation error");
          }))
          .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
            throw new PetException("Temporary external server error");
          }))
          .body(PetVO.class);
        assert petVOSave != null;
        stopped = !context.onResult(petVOSave);
      } catch(ResourceAccessException ex) {
        this.managerContextThrowException(context, ex);
      }
    } while(!stopped);
    context.onComplete();
    LOGGER.info("Successfully save");

    return petVOSave;
  }

  private void managerContextThrowException(Retry.Context<PetVO> context, Exception ex) {
    try {
      context.onError(ex);
    } catch (Exception e) {
      LOGGER.info(UNAVAILABLE_MESSAGE);
      throw new PetException(UNAVAILABLE_MESSAGE);
    }
  }


}
