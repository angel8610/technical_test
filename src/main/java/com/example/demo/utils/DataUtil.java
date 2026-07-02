package com.example.demo.utils;

import com.example.demo.vos.PetVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class DataUtil {

  private DataUtil() {

  }

  public static String buildRequestBody(PetVO petVO)
      throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.writeValueAsString(petVO);
  }

  public static PetVO convertToPetVO(String body) {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode jsonNode = objectMapper.readTree(body);
      if(!jsonNode.has("id")) {
        return null;
      }

      return objectMapper.readValue(body, PetVO.class);
    } catch (JsonProcessingException e) {
      return null;
    }
  }


}
