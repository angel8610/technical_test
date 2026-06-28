package com.example.demo.controllers;

import com.example.demo.dtos.PetDTO;
import com.example.demo.services.PetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
