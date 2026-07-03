package com.example.demo.services.impls;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.services.PetExternalService;
import com.example.demo.vos.PetVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetExternalServiceImplTest {

  private static final Long ID = 1L;
  private static final String PET_NAME = "PetName";
  private static final String STATUS_AVAILABLE = "available";

  private static Wire

  @Mock
  private RestClient restClient;

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
    petExternalService = new PetExternalServiceImpl(restClient);
  }

  @Test
  @SuppressWarnings("unchecked")
  void getPetByIdSuccessfully() {
    var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    var responseSpec = mock(RestClient.ResponseSpec.class);

    when(this.restClient.get()).thenReturn(uriSpec);
    when(uriSpec.uri("/{petId}", ID)).thenReturn(uriSpec);
    when(uriSpec.accept(any())).thenReturn(uriSpec);
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
  void getPetByIdNotFoundThrowPetNotFoundException() {
    when(this.restClient.get()).thenThrow(PetNotFoundException.class);
    assertThrows(PetNotFoundException.class, () -> this.petExternalService.findById(1L));
  }

  @Test
  void getPetByIdErrorServerThrowPetException() {
    when(this.restClient.get()).thenThrow(PetException.class);
    assertThrows(PetException.class, () -> this.petExternalService.findById(1L));
  }

  @Test
  void savePetSuccessfully() {
    var postSpec = mock(RestClient.RequestBodyUriSpec.class);
    var postResponseSpec = mock(RestClient.ResponseSpec.class);

    when(restClient.post()).thenReturn(postSpec);
    when(postSpec.contentType(any())).thenReturn(postSpec);
    when(postSpec.accept(any())).thenReturn(postSpec);
    when(postSpec.body(any(Object.class))).thenReturn(postSpec);
    when(postSpec.retrieve()).thenReturn(postResponseSpec);
    when(postResponseSpec.onStatus(any(), any())).thenReturn(postResponseSpec);
    when(postResponseSpec.body(PetVO.class)).thenReturn(
      this.createPetVO());

    PetVO petVO = this.petExternalService.save(this.createPetVO());
    assertNotNull(petVO);
    assertEquals(PET_NAME, petVO.getName());
    assertEquals(STATUS_AVAILABLE, petVO.getStatus());
  }

  @Test
  void savePetErrorServerThrowPetException() {
    when(this.restClient.post()).thenThrow(PetException.class);
    assertThrows(PetException.class, () -> this.petExternalService.save(this.createPetVO()));
  }

  private PetVO createPetVO() {
    var petVO = new PetVO();
    petVO.setId(PetExternalServiceImplTest.ID);
    petVO.setName(PetExternalServiceImplTest.PET_NAME);
    petVO.setStatus(STATUS_AVAILABLE);
    return petVO;
  }


}
