package com.example.demo.services.impls;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetExistException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.mappers.PetMapper;
import com.example.demo.services.PetService;
import com.example.demo.vos.PetVO;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PetServiceImpl implements PetService {

  private static final Logger LOGGER = Logger.getLogger(PetServiceImpl.class.getName());

  private final PetMapper petMapper;

  private final RestClient restClient;

  public PetServiceImpl(PetMapper petMapper, RestClient restClient) {
    this.petMapper = petMapper;
    this.restClient = restClient;
  }

  @Override
  public PetDTO getPetById(Long id) {
    Optional<PetDTO> petDTOOptional = this.searchExternalPetById(id);

    if(petDTOOptional.isEmpty()) {
      throw new PetNotFoundException("Pet not found with id: " + id);
    }

    var petDTO = petDTOOptional.get();
    LOGGER.info(petDTO.toString());
    return petDTO;
  }

  @Override
  public PetDTO savePet(PetSaveRequestDTO petSaveRequestDTO) {
    Long id = petSaveRequestDTO.id();

    Optional<PetDTO> petDTOSearch = this.searchExternalPetById(id);
    if(petDTOSearch.isPresent()) {
      throw new PetExistException("Pet already exists with id:" + id);
    }

    PetVO petVO = this.restClient.post()
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON)
      .body(this.petMapper.buildPetSaveRequestDTOToPetVO(petSaveRequestDTO))
      .retrieve()
      .onStatus(HttpStatusCode::is4xxClientError, ((request, response) -> {
        throw new PetException("External validation error");
      }))
      .onStatus(HttpStatusCode::is5xxServerError, ((request, response) -> {
        throw new PetException("Temporary external server error");
      }))
      .body(PetVO.class);

    return this.petMapper.buildPetVOToPetDTO(petVO);
  }

  private Optional<PetDTO> searchExternalPetById(Long id) {
    try {
      PetVO petVO = this.restClient.get()
        .uri("/{petId}", id)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .onStatus(HttpStatusCode::isError, (request, response) -> {
          throw new PetException("External server error");
        })
        .body(PetVO.class);
      return Optional.ofNullable(this.petMapper.buildPetVOToPetDTO(petVO));
    } catch(RestClientException | PetException ex) {
      return Optional.empty();
    }
  }


}
