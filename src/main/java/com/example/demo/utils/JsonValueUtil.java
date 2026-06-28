package com.example.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;

public final class JsonValueUtil {

  private JsonValueUtil() {
  }

  public static Long getValueLong(JsonNode jsonNode, String fieldName) {
    JsonNode node = jsonNode.get(fieldName);
    return (node == null || node.isNull()) ? null : node.asLong();
  }

  public static String getValueString(JsonNode jsonNode, String fieldName) {
    JsonNode node = jsonNode.get(fieldName);
    return (node == null || node.isNull()) ? null : node.asText();
  }


}
