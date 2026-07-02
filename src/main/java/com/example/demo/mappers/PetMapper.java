package com.example.demo.mappers;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.vos.PetVO;

public interface PetMapper {

  PetDTO buildPetVOToPetDTO(PetVO petVO);

  PetVO buildPetSaveRequestDTOToPetVO(PetSaveRequestDTO petSaveRequestDTO);


}
