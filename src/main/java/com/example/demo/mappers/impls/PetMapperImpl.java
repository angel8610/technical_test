package com.example.demo.mappers.impls;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.mappers.PetMapper;
import com.example.demo.vos.CategoryVO;
import com.example.demo.vos.PetVO;
import com.example.demo.vos.TagVO;

import java.util.Set;

public class PetMapperImpl implements PetMapper {

  @Override
  public PetDTO buildPetVOToPetDTO(PetVO petVO) {
    if(petVO == null) {
      return null;
    }

    return new PetDTO(petVO.getId(), petVO.getName(), petVO.getStatus());
  }

  @Override
  public PetVO buildPetSaveRequestDTOToPetVO(PetSaveRequestDTO petSaveRequestDTO) {
    var category = new CategoryVO(0L, "category");
    var photoUrls = Set.of("url1");
    var tags = Set.of(new TagVO(0L, "tag"));
    return new PetVO(petSaveRequestDTO.id(), category, petSaveRequestDTO.name(), photoUrls,
      tags, petSaveRequestDTO.status());
  }


}
