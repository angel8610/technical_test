package com.example.demo.services.impls;

import com.example.demo.configurations.HttpClientConfig;
import com.example.demo.configurations.HttpClientProperties;
import com.example.demo.dtos.PetDTO;
import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.services.PetService;
import com.example.demo.utils.HttpRequestUtil;
import com.example.demo.utils.JsonValueUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
      petDTOOptional = this.getPet(response.body());
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

  private Optional<PetDTO> getPet(String body) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(body);
    if(!jsonNode.has("id")) {
      return Optional.empty();
    }

    Long id = JsonValueUtil.getValueLong(jsonNode, "id");
    String name = JsonValueUtil.getValueString(jsonNode, "name");
    String status = JsonValueUtil.getValueString(jsonNode, "status");

    return Optional.of(new PetDTO(id, name, status));
  }


}
