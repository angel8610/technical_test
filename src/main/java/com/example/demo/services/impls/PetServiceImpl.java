package com.example.demo.services.impls;

import com.example.demo.configurations.HttpClientConfig;
import com.example.demo.configurations.HttpClientProperties;
import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetExistException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.mappers.PetMapper;
import com.example.demo.services.PetService;
import com.example.demo.utils.DataUtil;
import com.example.demo.utils.HttpRequestUtil;
import com.example.demo.vos.PetVO;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PetServiceImpl implements PetService {

  private static final Logger LOGGER = Logger.getLogger(PetServiceImpl.class.getName());

  private final HttpClientConfig connect;

  private final HttpClientProperties httpClientProperties;

  private final PetMapper petMapper;

  public PetServiceImpl(HttpClientConfig connect,
      HttpClientProperties httpClientProperties,
      PetMapper petMapper) {
    this.connect = connect;
    this.httpClientProperties = httpClientProperties;
    this.petMapper = petMapper;
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
    String[] headers = {"Accept", "application/json", "Content-Type", "application/json"};
    HttpResponse<String> response;
    PetDTO petDTO;
    Long id = petSaveRequestDTO.id();

    Optional<PetDTO> petDTOSearch = this.searchExternalPetById(id);
    if(petDTOSearch.isPresent()) {
      throw new PetExistException("Pet already exists with id:" + id);
    }

    try {
      var convertPetVO = this.petMapper.buildPetSaveRequestDTOToPetVO(petSaveRequestDTO);
      String jsonBody = DataUtil.buildRequestBody(convertPetVO);
      HttpRequest request = HttpRequestUtil.buildRequest(
       httpClientProperties.getBaseUrl())
       .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .headers(headers)
       .build();

      HttpClient client = connect.httpClient();
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
      var petVO = DataUtil.convertToPetVO(response.body());
      petDTO = this.petMapper.buildPetVOToPetDTO(petVO);
    } catch (Exception e) {
      throw new PetException("Exception while calling external API: " + e.getMessage());
    }

    if(petDTO == null) {
      throw new PetException("Pet could not be saved");
    }
    return petDTO;
  }

  private Optional<PetDTO> searchExternalPetById(Long id)  {
    HttpRequest request = HttpRequestUtil.buildRequest(
        httpClientProperties.getBaseUrl().concat("/".concat(id.toString())))
      .GET()
      .build();

    try {
      HttpClient client = connect.httpClient();
      var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      PetVO petVO = DataUtil.convertToPetVO(response.body());
      return Optional.ofNullable(this.petMapper.buildPetVOToPetDTO(petVO));
    } catch (Exception e) {
      LOGGER.warning("Error while calling external API: " + e.getMessage());
      throw new PetException("Error while calling external API");
    }
  }


}
