package com.example.demo.services.impls;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.dtos.PetSaveResponseDTO;
import com.example.demo.exceptions.PetExistException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.mappers.PetMapper;
import com.example.demo.services.PetExternalService;
import com.example.demo.services.PetService;
import com.example.demo.vos.CategoryVO;
import com.example.demo.vos.PetVO;
import com.example.demo.vos.TagVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PetServiceImpl implements PetService {

  private static final Logger LOGGER = Logger.getLogger(PetServiceImpl.class.getName());

  private final PetMapper petMapper;

  private final PetExternalService petExternalService;

  public PetServiceImpl(PetMapper petMapper, PetExternalService petExternalService) {
    this.petMapper = petMapper;
    this.petExternalService = petExternalService;
  }

  @Override
  public PetDTO getPetById(Long id) {
    Optional<PetVO> petVOOptional = this.petExternalService.findById(id);

    if(petVOOptional.isEmpty()) {
      throw new PetNotFoundException("Pet not found with id: " + id);
    }

    var petVO = petVOOptional.get();
    var petDTO = this.petMapper.buildPetVOToPetDTO(petVO);
    LOGGER.info(petDTO.toString());
    return petDTO;
  }

  @Override
  public PetSaveResponseDTO savePet(PetSaveRequestDTO petSaveRequestDTO) {
    Long id = petSaveRequestDTO.id();

    try {
      Optional<PetVO> petDTOSearch = this.petExternalService.findById(id);
      if(petDTOSearch.isPresent()) {
        throw new PetExistException("Pet already exists with id:" + id);
      }
    } catch (PetNotFoundException e) {
      LOGGER.info(e.getMessage());
    }

    PetVO petVO = this.petExternalService.save(this.buildPetSaveRequestDTOToPetVO(
      petSaveRequestDTO));
    var uuid = UUID.randomUUID().toString();
    var localDateTime = LocalDateTime.now(ZoneId.of("UTC"));
    assert petVO != null;
    return new PetSaveResponseDTO(uuid, localDateTime, true, petVO.getName());
  }

  private PetVO buildPetSaveRequestDTOToPetVO(PetSaveRequestDTO petSaveRequestDTO) {
    var category = new CategoryVO(0L, "category");
    var photoUrls = Set.of("url1");
    var tags = Set.of(new TagVO(0L, "tag"));
    return new PetVO(petSaveRequestDTO.id(), category, petSaveRequestDTO.name(), photoUrls,
      tags, petSaveRequestDTO.status());
  }


}
