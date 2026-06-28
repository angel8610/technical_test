package com.example.demo.services.impls;

import com.example.demo.configurations.HttpClientConfig;
import com.example.demo.configurations.HttpClientProperties;
import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.dtos.PetSaveResponseDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.services.PetService;
import com.example.demo.utils.DataUtil;
import com.example.demo.utils.HttpRequestUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PetServiceImpl implements PetService {

  private static final Logger LOGGER = Logger.getLogger(PetServiceImpl.class.getName());

  private final HttpClientConfig connect;

  private final HttpClientProperties httpClientProperties;

  public PetServiceImpl(HttpClientConfig connect, HttpClientProperties httpClientProperties) {
    this.connect = connect;
    this.httpClientProperties = httpClientProperties;
  }

  @Override
  public PetDTO getPetById(Long id) {
    Optional<PetDTO> petDTOOptional;
    HttpRequest request = HttpRequestUtil.buildRequest(
        httpClientProperties.getBaseUrl().concat("/".concat(id.toString())))
      .GET()
      .build();

    try {
      HttpClient client = connect.httpClient();
      var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      petDTOOptional = DataUtil.buildPetDTO(response.body());
    } catch (IOException | InterruptedException e) {
      LOGGER.warning("Error while calling external API: " + e.getMessage());
      throw new PetException("Error while calling external API");
    }

    if(petDTOOptional.isEmpty()) {
      throw new PetNotFoundException("Pet not found with id: " + id);
    }

    var petDTO = petDTOOptional.get();
    LOGGER.info(petDTO.toString());
    return petDTO;
  }

  @Override
  public PetSaveResponseDTO savePet(PetSaveRequestDTO petSaveRequestDTO) {
    String[] headers = {"Accept", "application/json", "Content-Type", "application/json"};
    HttpResponse<String> response;
    Optional<PetDTO> petDTOOptional;
    try {
      String jsonBody = DataUtil.buildRequestBody(petSaveRequestDTO);

      HttpRequest request = HttpRequestUtil.buildRequest(
       httpClientProperties.getBaseUrl())
       .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
        .headers(headers)
       .build();

      HttpClient client = connect.httpClient();
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
      petDTOOptional = DataUtil.buildPetDTO(response.body());
    } catch (Exception e) {
      throw new PetException("Exception while calling external API: " + e.getMessage());
    }

    if(petDTOOptional.isEmpty()) {
      throw new PetException("Pet could not be saved");
    }
    var petDTO = petDTOOptional.get();
    var uuid = UUID.randomUUID().toString();
    var localDateTime = LocalDateTime.now(ZoneId.of("UTC"));
    return new PetSaveResponseDTO(uuid, localDateTime, true, petDTO.getName());
  }


}
