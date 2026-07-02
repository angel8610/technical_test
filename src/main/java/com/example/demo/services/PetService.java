package com.example.demo.services;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;

public interface PetService {

  PetDTO getPetById(Long id);

  PetDTO savePet(PetSaveRequestDTO petSaveRequestDTO);


}
