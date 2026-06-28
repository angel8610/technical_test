package com.example.demo.utils;

import com.example.demo.dtos.PetDTO;
import com.example.demo.dtos.PetSaveRequestDTO;
import com.example.demo.vos.CategoryVO;
import com.example.demo.vos.PetVO;
import com.example.demo.vos.TagVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

public final class DataUtil {

  private DataUtil() {

  }

  public static Optional<PetDTO> buildPetDTO(String body) throws JsonProcessingException {
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

  public static String buildRequestBody(PetSaveRequestDTO petSaveRequestDTO)
      throws JsonProcessingException {
    PetVO petVO = buildPetVO(petSaveRequestDTO);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(petVO);
  }

  private static PetVO buildPetVO(PetSaveRequestDTO petSaveRequestDTO) {
    var category = new CategoryVO(0L, "category");
    var photoUrls = List.of("url1");
    var tags = List.of(new TagVO(0L, "tag"));
    return new PetVO(petSaveRequestDTO.id(), category, petSaveRequestDTO.name(), photoUrls,
      tags, petSaveRequestDTO.status());
  }


}
