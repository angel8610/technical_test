package com.example.demo.services.impls;

import com.example.demo.configurations.HttpClientConfig;
import com.example.demo.configurations.HttpClientProperties;
import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.dtos.PetSaveResponseDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PetServiceImplTest {

  private static final String BASE_URL = "https://petstore.swagger.io/v2/pet";

  private static final Long ID = 1L;

  private static final String PET_JSON_RESPONSE = """
      {
        "id": 1,
        "name": "PetName",
        "status": "available"
      }
      """;

  @Mock
  private HttpClientConfig httpClientConfig;

  @Mock
  private HttpClientProperties httpClientProperties;

  @Mock
  private HttpClient httpClient;

  @Mock
  private HttpResponse<String> httpResponse;

  private PetServiceImpl petService;

  @BeforeEach
  void setUp() {
    petService = new PetServiceImpl(httpClientConfig, httpClientProperties);
    when(httpClientConfig.httpClient()).thenReturn(httpClient);
    when(httpClientProperties.getBaseUrl()).thenReturn(BASE_URL);
  }

  @Test
  void getPetByIdWithValidIdShouldReturnPetDTO() throws IOException, InterruptedException {
    when(httpResponse.body()).thenReturn(PET_JSON_RESPONSE);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("PetName", result.getName());
    assertEquals("available", result.getStatus());
  }

  @Test
  void getPetByIdWithValidIdShouldBuildCorrectUrl() throws IOException, InterruptedException {
    String jsonResponse = "{\"id\": 123, \"name\": \"Buddy\", \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    petService.getPetById(123L);

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
    HttpRequest capturedRequest = requestCaptor.getValue();
    assertTrue(capturedRequest.uri().toString().contains("123"));
  }

  @Test
  void getPetByIdWithMissingIdFieldShouldThrowPetNotFoundException() throws IOException, InterruptedException {
    String jsonResponse = "{\"code\": 1, \"message\": \"error\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    assertThrows(PetNotFoundException.class, () -> petService.getPetById(ID));
  }

  @Test
  void getPetByIdWithIOExceptionShouldThrowPetException() throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Connection failed"));

    assertThrows(PetException.class, () -> petService.getPetById(ID));
  }

  @Test
  void getPetByIdWithInterruptedExceptionShouldThrowPetException() throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new InterruptedException("Thread interrupted"));

    assertThrows(PetException.class, () -> petService.getPetById(1L));
  }

  @Test
  void getPetByIdWithNullNameShouldHandleGracefully() throws IOException, InterruptedException {
    String jsonResponse = "{\"id\": 1, \"name\": null, \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertNull(result.getName());
    assertEquals("available", result.getStatus());
  }

  @Test
  void getPetByIdWithNullStatusShouldHandleGracefully() throws IOException, InterruptedException {
    Long petId = 1L;
    String jsonResponse = "{\"id\": 1, \"name\": \"PetName\", \"status\": null}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    PetDTO result = petService.getPetById(petId);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("PetName", result.getName());
    assertNull(result.getStatus());
  }

  @Test
  void getPetByIdWithEmptyNameShouldReturnEmptyString() throws IOException, InterruptedException {
    String jsonResponse = "{\"id\": 1, \"name\": \"\", \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals("", result.getName());
  }

  @Test
  void getPetByIdWithMissingNameFieldShouldReturnNullName() throws IOException, InterruptedException {
    String jsonResponse = "{\"id\": 1, \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertNull(result.getName());
  }

  @Test
  void getPetByIdWithMissingStatusFieldShouldReturnNullStatus() throws IOException, InterruptedException {
    String jsonResponse = "{\"id\": 1, \"name\": \"PetName\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertNull(result.getStatus());
  }

  @Test
  void getPetByIdShouldUseHttpClientConfigFromConstructor() throws IOException, InterruptedException {
    when(httpResponse.body()).thenReturn(PET_JSON_RESPONSE);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    petService.getPetById(ID);

    verify(httpClientConfig, times(1)).httpClient();
  }

  @Test
  void getPetByIdShouldUseHttpClientPropertiesFromConstructor() throws IOException, InterruptedException {
    when(httpResponse.body()).thenReturn(PET_JSON_RESPONSE);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    petService.getPetById(ID);

    verify(httpClientProperties, atLeastOnce()).getBaseUrl();
  }

  @Test
  void getPetByIdWithInvalidJsonShouldThrowPetException() throws IOException, InterruptedException {
    String invalidJson = "{ invalid json }";
    
    when(httpResponse.body()).thenReturn(invalidJson);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    assertThrows(PetException.class, () -> petService.getPetById(ID));
  }

  void savePetWithValidRequestSuccess() throws IOException, InterruptedException {
    PetSaveRequestDTO requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    String jsonResponse = "{\"id\": 1, \"name\": \"PetName\", \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    PetSaveResponseDTO result = petService.savePet(requestDTO);

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
    HttpRequest capturedRequest = requestCaptor.getValue();
    var headers = capturedRequest.headers();

    assertEquals("POST", capturedRequest.method());
    assertNotNull(headers.firstValue("Accept"));
    assertNotNull(headers.firstValue("Content-Type"));
    assertEquals("application/json", headers.firstValue("Accept").orElse(null));
    assertEquals("application/json", headers.firstValue("Content-Type").orElse(null));
    assertEquals("PetName", result.getName());
    assertNotNull(capturedRequest.uri());
    assertTrue(capturedRequest.uri().toString().contains(BASE_URL));
    assertTrue(result.isStatus());
    assertNotNull(result.getTransactionId());
    assertFalse(result.getTransactionId().isEmpty());
    assertNotNull(result.getDateCreated());
    assertEquals(ZoneId.of("UTC"), result.getDateCreated().atZone(ZoneId.of("UTC")).getZone());
  }

  @Test
  void savePetWithInvalidResponseShouldThrowPetException() throws IOException, InterruptedException {
    PetSaveRequestDTO requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    String invalidJson = "{\"code\": 1, \"message\": \"error\"}";
    
    when(httpResponse.body()).thenReturn(invalidJson);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }

  @Test
  void savePetWithIOExceptionShouldThrowPetException() throws IOException, InterruptedException {
    PetSaveRequestDTO requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Connection failed"));

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }

  @Test
  void savePetWithInterruptedExceptionShouldThrowPetException() throws IOException, InterruptedException {
    PetSaveRequestDTO requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new InterruptedException("Thread interrupted"));

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }

  @Test
  void savePetWithNullJsonResponseShouldThrowPetException() throws IOException, InterruptedException {
    PetSaveRequestDTO requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    String invalidJson = "{ invalid json }";
    
    when(httpResponse.body()).thenReturn(invalidJson);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }


}
