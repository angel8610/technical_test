package com.example.demo.services.impls;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.exceptions.PetExistException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.mappers.PetMapper;
import com.example.demo.vos.PetVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

  private static final Long ID = 1L;
  private static final String PET_NAME = "PetName";
  private static final String STATUS_AVAILABLE = "available";

  @Mock
  private RestClient restClient;

  @Mock
  private PetMapper petMapper;

  private PetServiceImpl petService;

  @BeforeEach
  void setUp() {
    petService = new PetServiceImpl(petMapper, restClient);
  }

  private PetVO createPetVO(String name, String status) {
    var petVO = new PetVO();
    petVO.setId(PetServiceImplTest.ID);
    petVO.setName(name);
    petVO.setStatus(status);
    return petVO;
  }

  @Test
  void getPetByIdWithValidIdShouldReturnPetDTO() {
    var petVO = createPetVO(PET_NAME, STATUS_AVAILABLE);
    var expectedDTO = new PetDTO(ID, PET_NAME, STATUS_AVAILABLE);

    setupGetPetMock(petVO);
    when(petMapper.buildPetVOToPetDTO(petVO)).thenReturn(expectedDTO);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(ID, result.getId());
    assertEquals(PET_NAME, result.getName());
    assertEquals(STATUS_AVAILABLE, result.getStatus());
  }

  @Test
  void getPetByIdWithValidIdShouldCallMapper() {
    var petVO = createPetVO(PET_NAME, STATUS_AVAILABLE);
    var expectedDTO = new PetDTO(ID, PET_NAME, STATUS_AVAILABLE);

    setupGetPetMock(petVO);
    when(petMapper.buildPetVOToPetDTO(petVO)).thenReturn(expectedDTO);

    petService.getPetById(ID);

    verify(petMapper).buildPetVOToPetDTO(petVO);
  }

  @Test
  void getPetByIdWithNullResponseShouldThrowPetNotFoundException() {
    setupGetPetMock(null);
    when(petMapper.buildPetVOToPetDTO(null)).thenReturn(null);

    assertThrows(PetNotFoundException.class, () -> petService.getPetById(ID));
  }

  @Test
  void getPetByIdWithNullNameShouldHandleGracefully() {
    var petVO = createPetVO(null, STATUS_AVAILABLE);
    var expectedDTO = new PetDTO(ID, null, STATUS_AVAILABLE);

    setupGetPetMock(petVO);
    when(petMapper.buildPetVOToPetDTO(petVO)).thenReturn(expectedDTO);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertNull(result.getName());
    assertEquals(STATUS_AVAILABLE, result.getStatus());
  }

  @Test
  void getPetByIdWithNullStatusShouldHandleGracefully() {
    var petVO = createPetVO(PET_NAME, null);
    var expectedDTO = new PetDTO(ID, PET_NAME, null);

    setupGetPetMock(petVO);
    when(petMapper.buildPetVOToPetDTO(petVO)).thenReturn(expectedDTO);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(PET_NAME, result.getName());
    assertNull(result.getStatus());
  }

  @Test
  void getPetByIdWithEmptyNameShouldReturnEmptyString() {
    var petVO = createPetVO("", STATUS_AVAILABLE);
    var expectedDTO = new PetDTO(ID, "", STATUS_AVAILABLE);

    setupGetPetMock(petVO);
    when(petMapper.buildPetVOToPetDTO(petVO)).thenReturn(expectedDTO);

    PetDTO result = petService.getPetById(ID);

    assertNotNull(result);
    assertEquals("", result.getName());
  }

  @Test
  void savePetWithValidRequestShouldSucceed() {
    var requestDTO = new PetSaveRequestDTO(ID, PET_NAME, STATUS_AVAILABLE);
    var createdPetVO = createPetVO(PET_NAME, STATUS_AVAILABLE);
    var expectedDTO = new PetDTO(ID, PET_NAME, STATUS_AVAILABLE);

    setupGetPetMock(null);
    when(petMapper.buildPetVOToPetDTO(null)).thenReturn(null);

    var postSpec = mock(RestClient.RequestBodyUriSpec.class);
    var postResponseSpec = mock(RestClient.ResponseSpec.class);

    when(restClient.post()).thenReturn(postSpec);
    when(postSpec.contentType(any())).thenReturn(postSpec);
    when(postSpec.accept(any())).thenReturn(postSpec);
    when(postSpec.body(any(Object.class))).thenReturn(postSpec);
    when(postSpec.retrieve()).thenReturn(postResponseSpec);
    when(postResponseSpec.onStatus(any(), any())).thenReturn(postResponseSpec);
    when(postResponseSpec.body(PetVO.class)).thenReturn(createdPetVO);

    when(petMapper.buildPetSaveRequestDTOToPetVO(requestDTO)).thenReturn(createdPetVO);
    when(petMapper.buildPetVOToPetDTO(createdPetVO)).thenReturn(expectedDTO);

    PetDTO result = petService.savePet(requestDTO);

    assertNotNull(result);
    assertEquals(ID, result.getId());
    assertEquals(PET_NAME, result.getName());
  }

  @Test
  void savePetWithExistingPetShouldThrowPetExistException() {
    var requestDTO = new PetSaveRequestDTO(ID, PET_NAME, STATUS_AVAILABLE);
    var existingPetVO = createPetVO(PET_NAME, STATUS_AVAILABLE);
    var existingDTO = new PetDTO(ID, PET_NAME, STATUS_AVAILABLE);

    setupGetPetMock(existingPetVO);
    when(petMapper.buildPetVOToPetDTO(existingPetVO)).thenReturn(existingDTO);

    assertThrows(PetExistException.class, () -> petService.savePet(requestDTO));
  }

  @Test
  void savePetWithPostExceptionShouldThrowPetException() {
    var requestDTO = new PetSaveRequestDTO(ID, PET_NAME, STATUS_AVAILABLE);
    var petVO = createPetVO(PET_NAME, STATUS_AVAILABLE);

    setupGetPetMock(null);
    when(petMapper.buildPetVOToPetDTO(null)).thenReturn(null);

    var postSpec = mock(RestClient.RequestBodyUriSpec.class);
    var postResponseSpec = mock(RestClient.ResponseSpec.class);

    when(restClient.post()).thenReturn(postSpec);
    when(postSpec.contentType(any())).thenReturn(postSpec);
    when(postSpec.accept(any())).thenReturn(postSpec);
    when(postSpec.body(any(Object.class))).thenReturn(postSpec);
    when(postSpec.retrieve()).thenReturn(postResponseSpec);
    when(postResponseSpec.onStatus(any(), any())).thenReturn(postResponseSpec);
    when(postResponseSpec.body(PetVO.class)).thenThrow(new RestClientException("Error"));

    when(petMapper.buildPetSaveRequestDTOToPetVO(requestDTO)).thenReturn(petVO);

    assertThrows(RestClientException.class, () -> petService.savePet(requestDTO));
  }

  @SuppressWarnings("unchecked")
  private void setupGetPetMock(PetVO responsePetVO) {
    var uriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    var responseSpec = mock(RestClient.ResponseSpec.class);

    when(restClient.get()).thenReturn(uriSpec);
    when(uriSpec.uri("/{petId}", ID)).thenReturn(uriSpec);
    when(uriSpec.accept(any())).thenReturn(uriSpec);
    when(uriSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.body(PetVO.class)).thenReturn(responsePetVO);
  }


}
