package com.example.demo.mappers.impls;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.mappers.PetMapper;
import com.example.demo.vos.CategoryVO;
import com.example.demo.vos.PetVO;
import com.example.demo.vos.TagVO;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PetMapperImpl implements PetMapper {

  @Override
  public PetDTO buildPetVOToPetDTO(PetVO petVO) {
    if(petVO == null) {
      return null;
    }

    return new PetDTO(petVO.getId(), petVO.getName(), petVO.getStatus());
  }


}
