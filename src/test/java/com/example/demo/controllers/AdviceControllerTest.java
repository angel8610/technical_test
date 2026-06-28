package com.example.demo.controllers;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdviceControllerTest {

  @Test
  void handlePetNotFoundReturnsNotFoundBody() {
    AdviceController controller = new AdviceController();
    String msg = "Pet not found with id: 123";

    ResponseEntity<Map<String, Object>> response = controller.handlePetNotFound(new PetNotFoundException(msg));

    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals(404, body.get("code"));
    assertEquals(msg, body.get("message"));
  }

  @Test
  void handlePetExceptionReturnsBadRequestBody() {
    AdviceController controller = new AdviceController();
    String msg = "Invalid request";

    ResponseEntity<Map<String, Object>> response = controller.handlePetException(new PetException(msg));

    assertNotNull(response);
    assertEquals(400, response.getStatusCodeValue());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals(400, body.get("code"));
    assertEquals(msg, body.get("message"));
  }


}
