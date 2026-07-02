package com.example.demo.controllers;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.dtos.PetSaveResponseDTO;
import com.example.demo.services.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@RestController
@RequestMapping("api/pet")
public class PetController {

  private final PetService petService;

  public PetController(PetService petService) {
    this.petService = petService;
  }

  @GetMapping(value = "/{petId}", produces = "application/json")
  public ResponseEntity<PetDTO> getPet(@PathVariable Long petId){
    return ResponseEntity.ok(this.petService.getPetById(petId));
  }

  @PostMapping(produces = "application/json")
  public ResponseEntity<PetSaveResponseDTO> savePet(
      @Valid @RequestBody PetSaveRequestDTO petSaveRequestDTO) {
    PetDTO petDTO = this.petService.savePet(petSaveRequestDTO);
    return ResponseEntity.status(HttpStatus.CREATED).body(
      this.buildResponseSave(petDTO));
  }

  private PetSaveResponseDTO buildResponseSave(PetDTO petDTO) {
    var uuid = UUID.randomUUID().toString();
    var localDateTime = LocalDateTime.now(ZoneId.of("UTC"));
    return new PetSaveResponseDTO(uuid, localDateTime, true, petDTO.getName());
  }


}
