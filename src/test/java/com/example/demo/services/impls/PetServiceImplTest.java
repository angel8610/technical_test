package com.example.demo.services.impls;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.mappers.PetMapper;
import com.example.demo.vos.PetVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

  private static final Long ID = 1L;

  private static final String PET_JSON_RESPONSE = """
      {
        "id": 1,
        "name": "PetName",
        "status": "available"
      }
      """;

  private static final PetDTO PET_DTO = new PetDTO(1L, "PetName", "available");

  @Mock
  private RestClient restClient;

  @Mock
  private HttpResponse<String> httpResponse;

  @Mock
  private PetMapper petMapper;

  private PetServiceImpl petService;

  @BeforeEach
  void setUp() {
    petService = new PetServiceImpl(petMapper, restClient);
  }

  /**@Test
  void getPetByIdWithValidIdShouldReturnPetDTO() {
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(PET_DTO);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertEquals("PetName", result.getName());
    assertEquals("available", result.getStatus());
  }

  @Test
  void getPetByIdWithValidIdShouldBuildCorrectUrl() throws IOException, InterruptedException {
    var jsonResponse = "{\"id\": 123, \"name\": \"PetName\", \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(new PetDTO(
      123L, "PetName", "available"));

    petService.getPetById(123L);

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
    HttpRequest capturedRequest = requestCaptor.getValue();
    assertTrue(capturedRequest.uri().toString().contains("123"));
  }

  @Test
  void getPetByIdWithMissingIdFieldShouldThrowPetNotFoundException() throws IOException, InterruptedException {
    var jsonResponse = "{\"code\": 1, \"message\": \"error\"}";
    
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
    var jsonResponse = "{\"id\": 1, \"name\": null, \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(new PetDTO(
      ID, null, "available"));

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(1L, result.getId());
    assertNull(result.getName());
    assertEquals("available", result.getStatus());
  }

  @Test
  void getPetByIdWithNullStatusShouldHandleGracefully() throws IOException, InterruptedException {
    var jsonResponse = "{\"id\": 1, \"name\": \"PetName\", \"status\": null}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(new PetDTO(
      ID, "PetName", null));

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(ID, result.getId());
    assertEquals("PetName", result.getName());
    assertNull(result.getStatus());
  }

  @Test
  void getPetByIdWithEmptyNameShouldReturnEmptyString() throws IOException, InterruptedException {
    var jsonResponse = "{\"id\": 1, \"name\": \"\", \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(new PetDTO(
      ID, "", "available"));

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals("", result.getName());
  }

  @Test
  void getPetByIdWithMissingNameFieldShouldReturnNullName() throws IOException, InterruptedException {
    var jsonResponse = "{\"id\": 1, \"status\": \"available\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(new PetDTO(
      ID, null, "available"));

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertNull(result.getName());
  }

  @Test
  void getPetByIdWithMissingStatusFieldShouldReturnNullStatus() throws IOException, InterruptedException {
    var jsonResponse = "{\"id\": 1, \"name\": \"PetName\"}";
    
    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(new PetDTO(
      ID, "PetName", null));

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertNull(result.getStatus());
  }

  @Test
  void getPetByIdShouldUseHttpClientConfigFromConstructor() throws IOException, InterruptedException {
    when(httpResponse.body()).thenReturn(PET_JSON_RESPONSE);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(PET_DTO);

    petService.getPetById(ID);

    verify(httpClientConfig, times(1)).httpClient();
  }

  @Test
  void getPetByIdShouldUseHttpClientPropertiesFromConstructor() throws IOException, InterruptedException {
    when(httpResponse.body()).thenReturn(PET_JSON_RESPONSE);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(PET_DTO);

    petService.getPetById(ID);

    verify(httpClientProperties, atLeastOnce()).getBaseUrl();
  }

  @Test
  void savePetWithValidRequestSuccess() throws IOException, InterruptedException {
    var requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    var jsonResponse = "{\"id\": 1, \"name\": \"PetName\", \"status\": \"available\"}";

    when(httpResponse.body()).thenReturn(jsonResponse);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(this.petMapper.buildPetVOToPetDTO(any()))
        .thenReturn(null)
        .thenReturn(PET_DTO)
        .thenReturn(PET_DTO);

    PetDTO result = petService.savePet(requestDTO);

    ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
    verify(httpClient, times(2)).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
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
  }

  @Test
  void savePetWithInvalidResponseShouldThrowPetException() throws IOException, InterruptedException {
    var requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    var invalidJson = "{\"code\": 1, \"message\": \"error\"}";
    
    when(httpResponse.body()).thenReturn(invalidJson);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }

  @Test
  void savePetWithIOExceptionShouldThrowPetException() throws IOException, InterruptedException {
    var requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");

    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("Connection failed"));

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }

  @Test
  void savePetWithInterruptedExceptionShouldThrowPetException() throws IOException, InterruptedException {
    var requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");

    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new InterruptedException("Thread interrupted"));

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }

  @Test
  void savePetWithNullJsonResponseShouldThrowPetException() throws IOException, InterruptedException {
    var requestDTO = new PetSaveRequestDTO(ID, "PetName", "available");
    String invalidJson = "{ invalid json }";
    
    when(httpResponse.body()).thenReturn(invalidJson);
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);

    assertThrows(PetException.class, () -> petService.savePet(requestDTO));
  }*/


}
