package com.example.demo.controllers;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.dtos.PetSaveResponseDTO;
import com.example.demo.services.PetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/pet")
public class PetController {

  private final PetService petService;

  public PetController(PetService petService) {
    this.petService = petService;
  }

  @GetMapping(value = "/{petId}", produces = "application/json")
  public ResponseEntity<PetDTO> getPet(@PathVariable Long petId) {
    return ResponseEntity.ok(this.petService.getPetById(petId));
  }

  @PostMapping(produces = "application/json")
  public ResponseEntity<PetSaveResponseDTO> savePet(
      @Valid @RequestBody PetSaveRequestDTO petSaveRequestDTO) {
    return ResponseEntity.ok(this.petService.savePet(petSaveRequestDTO));
  }


}
