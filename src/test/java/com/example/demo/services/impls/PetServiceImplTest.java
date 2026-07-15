package com.example.demo.services.impls;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.dtos.PetSaveResponseDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetExistException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.mappers.PetMapper;
import com.example.demo.services.PetExternalService;
import com.example.demo.vos.PetVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetServiceImplTest {

  private static final Long ID = 1L;
  private static final String PET_NAME = "PetName";
  private static final String STATUS_AVAILABLE = "available";

  @Mock
  private PetExternalService petExternalService;
  @Mock
  private PetMapper petMapper;

  private PetServiceImpl petService;

  @BeforeEach
  void setUp() {
    this.petService = new PetServiceImpl(petMapper, petExternalService);
  }

  @Test
  void getPetByIdWithValidIdShouldReturnPetDTO() {
    var petVO = createPetVO();
    var expectedDTO = new PetDTO(ID, PET_NAME, STATUS_AVAILABLE);

    when(this.petExternalService.findById(anyLong())).thenReturn(Optional.of(petVO));
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(expectedDTO);

    PetDTO result = this.petService.getPetById(ID);

    assertNotNull(result);
    assertEquals(ID, result.getId());
    assertEquals(PET_NAME, result.getName());
    assertEquals(STATUS_AVAILABLE, result.getStatus());
  }

  @Test
  void getPetByIdWithValidIdShouldCallMapper() {
    var petVO = createPetVO();
    var expectedDTO = new PetDTO(ID, PET_NAME, STATUS_AVAILABLE);

    when(this.petExternalService.findById(anyLong())).thenReturn(Optional.of(petVO));
    when(this.petMapper.buildPetVOToPetDTO(any())).thenReturn(expectedDTO);

    this.petService.getPetById(ID);

    verify(this.petMapper).buildPetVOToPetDTO(petVO);
  }

  @Test
  void getPetByIdNotFoundThrowPetNotFoundException() {
    when(this.petExternalService.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(PetNotFoundException.class, () -> this.petService.getPetById(ID));
  }

  @Test
  void getPetByIdErrorServerResponseThrowPetException() {
    when(this.petExternalService.findById(anyLong())).thenThrow(PetException.class);
    assertThrows(PetException.class, () -> this.petService.getPetById(ID));
  }

  @Test
  void savePetSuccessfully() {
    var requestDTO = new PetSaveRequestDTO(ID, PET_NAME, STATUS_AVAILABLE);
    var createdPetVO = createPetVO();

    when(this.petExternalService.findById(anyLong())).thenThrow(PetNotFoundException.class);
    when(this.petExternalService.save(any())).thenReturn(createdPetVO);

    PetSaveResponseDTO result = this.petService.savePet(requestDTO);

    assertNotNull(result);
    assertNotNull(result.getDateCreated());
    assertNotNull(result.getTransactionId());
    assertEquals(PET_NAME, result.getName());
  }

  @Test
  void savePetWithExistingPetShouldThrowPetExistException() {
    var requestDTO = new PetSaveRequestDTO(ID, PET_NAME, STATUS_AVAILABLE);
    var existingPetVO = createPetVO();

    when(this.petExternalService.findById(anyLong())).thenReturn(Optional.of(existingPetVO));

    assertThrows(PetExistException.class, () -> this.petService.savePet(requestDTO));
  }

  @Test
  void savePetErrorServerShouldThrowPetException() {
    var requestDTO = new PetSaveRequestDTO(ID, PET_NAME, STATUS_AVAILABLE);

    when(this.petExternalService.findById(anyLong())).thenThrow(PetNotFoundException.class);
    when(this.petExternalService.save(any())).thenThrow(PetException.class);

    assertThrows(PetException.class, () -> this.petService.savePet(requestDTO));
  }

  private PetVO createPetVO() {
    var petVO = new PetVO();
    petVO.setId(PetServiceImplTest.ID);
    petVO.setName(PetServiceImplTest.PET_NAME);
    petVO.setStatus(PetServiceImplTest.STATUS_AVAILABLE);
    return petVO;
  }


}
