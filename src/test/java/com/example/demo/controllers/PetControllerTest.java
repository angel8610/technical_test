package com.example.demo.controllers;

import com.example.demo.dtos.PetDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.services.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetControllerTest {

  private static final Long ID = 1L;

  @Mock
  private PetService petService;

  private PetController petController;

  @BeforeEach
  void setUp() {
    petController = new PetController(petService);
  }

  @Test
  void getPetReturnsOkWithPetDTO() {
    PetDTO dto = new PetDTO(ID, "PetName", "available");
    when(petService.getPetById(ID)).thenReturn(dto);

    ResponseEntity<PetDTO> resp = petController.getPet(ID);

    assertNotNull(resp);
    assertEquals(200, resp.getStatusCodeValue());
    assertEquals(dto, resp.getBody());
    verify(petService, times(1)).getPetById(ID);
  }

  @Test
  void getPetShouldCallServiceWithCorrectId() {
    PetDTO dto = new PetDTO(ID, "PetName", "available");
    when(petService.getPetById(ID)).thenReturn(dto);

    petController.getPet(ID);

    verify(petService).getPetById(eq(ID));
  }

  @Test
  void getPetWhenServiceThrowsPetNotFoundShouldPropagate() {
    when(petService.getPetById(ID)).thenThrow(new PetNotFoundException("Not found"));

    assertThrows(PetNotFoundException.class, () -> petController.getPet(ID));
    verify(petService).getPetById(ID);
  }

  @Test
  void getPetWhenServiceThrowsPetExceptionShouldPropagate() {
    when(petService.getPetById(ID)).thenThrow(new PetException("Bad request"));

    assertThrows(PetException.class, () -> petController.getPet(ID));
    verify(petService).getPetById(ID);
  }


}
