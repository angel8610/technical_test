package com.example.demo.services.impls;

import com.example.demo.exceptions.PetClientException;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.exceptions.PetServerException;
import com.example.demo.services.PetExternalService;
import com.example.demo.vos.PetVO;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetExternalServiceImplTest {

  private static final String PET_NAME = "PetName";
  private static final String STATUS_AVAILABLE = "available";

  @Mock
  private RestClient restClient;
  @Mock
  private RestClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private RestClient.ResponseSpec responseSpec;

  private PetExternalService petExternalService;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public RestClient restClient() {
      return RestClient.builder()
        .baseUrl("http://localhost:8089")
        .build();
    }
  }

  @BeforeEach
  void setUp() {
    petExternalService = new PetExternalServiceImpl(restClient, RetryRegistry.ofDefaults());
  }

  @Test
  void findPetByIdSuccessfully() {
    var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);

    setupRestClientGet(uriSpec, 1L);
    when(uriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.body(PetVO.class)).thenReturn(
      this.createPetVO());

    PetVO petVO = this.petExternalService.findById(1L).orElse(null);
    assertNotNull(petVO);
    assertEquals(PET_NAME, petVO.getName());
    assertEquals(STATUS_AVAILABLE, petVO.getStatus());
  }

  @Test
  void findByIdPetErrorConnectServerSecondConnectSuccessfully() {
    setupRestClientGet(requestBodyUriSpec, 1L);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.body(PetVO.class))
      .thenThrow(ResourceAccessException.class)
      .thenReturn(createPetVO());

    PetVO petVO = this.petExternalService.findById(1L).orElse(null);

    assertNotNull(petVO);
    verify(restClient, times(2)).get();
    verify(requestBodyUriSpec, times(2)).retrieve();
  }

  @Test
  void findByIdPetErrorConnectServerShouldThrowPetException() {
    setupRestClientGet(requestBodyUriSpec, 1L);
    when(requestBodyUriSpec.retrieve()).thenThrow(
      new ResourceAccessException("Connection refused"));

    PetException petException = assertThrows(PetException.class, () ->
      this.petExternalService.findById(1L));

    verify(restClient, times(3)).get();
    verify(requestBodyUriSpec, times(3)).retrieve();
    assertTrue(petException.getMessage().contains("unavailable."));
  }

  @Test
  void findPetByIdNotFoundThrowPetNotFoundException() {
    var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);

    setupRestClientGet(uriSpec, 1L);
    when(uriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
      ErrorHandler errorHandler = invocation.getArgument(1);
      ClientHttpResponse clientHttpResponse = mock(ClientHttpResponse.class);
      errorHandler.handle(mock(ClientHttpRequest.class), clientHttpResponse);
      return responseSpec;
    });

    assertThrows(PetException.class, () -> this.petExternalService.findById(1L));
  }

  @Test
  void findPetByIdWhenNotFoundThrowPetException() {
    setupRestClientGet(requestBodyUriSpec, 3L);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    callExternalServiceFindPetById(3L);

    ArgumentCaptor<ErrorHandler> handlerCaptor = ArgumentCaptor.forClass(ErrorHandler.class);
    verify(responseSpec, times(3)).onStatus(
      any(), handlerCaptor.capture());
    ErrorHandler errorHandler = handlerCaptor.getAllValues().get(0);

    PetNotFoundException petNotFoundException = assertThrows(PetNotFoundException.class, () ->
      errorHandler.handle(mock(ClientHttpRequest.class), mock(ClientHttpResponse.class))
    );

    assertNotNull(petNotFoundException);
  }

  @Test
  void findByIdShouldRetryWhenOccursError4xx() {
    setupRestClientGet(requestBodyUriSpec, 1L);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    callExternalServiceFindPetById(1L);

    ArgumentCaptor<ErrorHandler> handlerCaptor = ArgumentCaptor.forClass(ErrorHandler.class);
    verify(responseSpec, times(3)).onStatus(
      any(), handlerCaptor.capture());
    ErrorHandler errorHandler = handlerCaptor.getAllValues().get(1);

    PetClientException petClientException = assertThrows(PetClientException.class, () ->
      errorHandler.handle(mock(ClientHttpRequest.class), mock(ClientHttpResponse.class))
    );

    assertNotNull(petClientException);
  }

  @Test
  void findByIdShouldRetryWhenOccursError5xx() {
    setupRestClientGet(requestBodyUriSpec, 2L);
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    callExternalServiceFindPetById(2L);

    ArgumentCaptor<ErrorHandler> handlerCaptor = ArgumentCaptor.forClass(ErrorHandler.class);
    verify(responseSpec, times(3)).onStatus(
      any(), handlerCaptor.capture());
    ErrorHandler errorHandler = handlerCaptor.getAllValues().get(2);

    PetServerException petServerException = assertThrows(PetServerException.class, () ->
      errorHandler.handle(mock(ClientHttpRequest.class), mock(ClientHttpResponse.class))
    );

    assertNotNull(petServerException);
  }

  @Test
  void savePetSuccessfully() {
    setupRestClientPost();
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.body(PetVO.class)).thenReturn(
      this.createPetVO());

    PetVO petVO = this.petExternalService.save(this.createPetVO());
    assertNotNull(petVO);
    assertEquals(PET_NAME, petVO.getName());
    assertEquals(STATUS_AVAILABLE, petVO.getStatus());
  }

  @Test
  void savePetErrorServerThrowPetException() {
    PetVO petVO = this.createPetVO();
    when(this.restClient.post()).thenThrow(PetException.class);

    assertThrows(PetException.class, () ->
      this.petExternalService.save(petVO));
  }

  @Test
  void savePetErrorConnectServerSecondConnectSuccessfully() {
    setupRestClientPost();
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.body(PetVO.class))
      .thenThrow(ResourceAccessException.class)
      .thenReturn(createPetVO());

    PetVO petVO = this.petExternalService.save(createPetVO());

    assertNotNull(petVO);
    verify(restClient, times(2)).post();
    verify(requestBodyUriSpec, times(2)).retrieve();
  }

  @Test
  void savePetErrorConnectServerShouldThrowPetException() {
    PetVO petVO = this.createPetVO();

    setupRestClientPost();
    when(requestBodyUriSpec.retrieve()).thenThrow(
      new ResourceAccessException("Connection refused"));

    PetException petException = assertThrows(PetException.class, () ->
      this.petExternalService.save(petVO));

    verify(restClient, times(3)).post();
    verify(requestBodyUriSpec, times(3)).retrieve();
    assertTrue(petException.getMessage().contains("unavailable."));
  }

  @Test
  void saveShouldNotRetryWhenOccursError4xx() {
    PetVO petVO = createPetVO();

    setupRestClientPost();
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenAnswer(invocation -> {
      ErrorHandler errorHandler = invocation.getArgument(1);
      errorHandler.handle(mock(ClientHttpRequest.class), mock(ClientHttpResponse.class));
      return responseSpec;
    });

    assertThrows(PetException.class, () -> this.petExternalService.save(petVO));

    verify(restClient, times(1)).post();
  }

  @Test
  void saveShouldRetryWhenOccursError5xx() {
    PetVO petVO = createPetVO();

    setupRestClientPost();
    when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    try {
      this.petExternalService.save(petVO);
    } catch(Exception e) {
      // Catch the exception to allow verification of the error handler
    }

    ArgumentCaptor<ErrorHandler> handlerCaptor = ArgumentCaptor.forClass(ErrorHandler.class);
    verify(responseSpec, times(2)).onStatus(
      any(), handlerCaptor.capture());
    ErrorHandler errorHandler = handlerCaptor.getAllValues().get(1);

    PetServerException petServerException = assertThrows(PetServerException.class, () ->
      errorHandler.handle(mock(ClientHttpRequest.class), mock(ClientHttpResponse.class))
    );

    assertNotNull(petServerException);
  }

  private PetVO createPetVO() {
    var petVO = new PetVO();
    petVO.setId(1L);
    petVO.setName(PetExternalServiceImplTest.PET_NAME);
    petVO.setStatus(STATUS_AVAILABLE);
    return petVO;
  }

  @SuppressWarnings({"unchecked", "rawrite"})
  private void setupRestClientGet(RestClient.RequestHeadersUriSpec uriSpec, Long petId) {
    when(this.restClient.get()).thenReturn(uriSpec);
    when(uriSpec.uri("/{petId}", petId)).thenReturn(uriSpec);
    when(uriSpec.accept(any())).thenReturn(uriSpec);
  }

  private void callExternalServiceFindPetById(Long petId) {
    try {
      this.petExternalService.findById(petId);
    } catch(Exception e) {
      // Catch the exception to allow verification of the error handler
    }
  }

  private void setupRestClientPost() {
    when(restClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.accept(any())).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.body(any(Object.class))).thenReturn(requestBodyUriSpec);
  }


}
