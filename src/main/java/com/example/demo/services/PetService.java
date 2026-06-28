package com.example.demo.services;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.dtos.PetSaveResponseDTO;

public interface PetService {

  PetDTO getPetById(Long id);

  PetSaveResponseDTO savePet(PetSaveRequestDTO petSaveRequestDTO);


}
