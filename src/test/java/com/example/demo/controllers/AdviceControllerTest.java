package com.example.demo.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class AdviceControllerTest {

  @Test
  void handlePetNotFoundReturnsNotFoundBody() {
    AdviceController controller = new AdviceController();
    String msg = "Pet not found with id: 123";

    ResponseEntity<Map<String, Object>> response = controller.handlePetNotFound(new PetNotFoundException(msg));

    assertNotNull(response);
    assertEquals(404, response.getStatusCode().value());
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
    assertEquals(500, response.getStatusCode().value());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals(500, body.get("code"));
    assertEquals(msg, body.get("message"));
  }

  @Test
  void handleMethodArgumentNotValidExceptionReturnsErrors() {
    AdviceController controller = new AdviceController();
    BindingResult bindingResult = mock(BindingResult.class);
    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    when(exception.getBindingResult()).thenReturn(bindingResult);

    List<FieldError> fieldErrors = new ArrayList<>();
    fieldErrors.add(new FieldError(
      "object", "name", "Name is required"));
    fieldErrors.add(new FieldError(
      "object", "status", "Status is required"));
    when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

    ResponseEntity<Map<String, Object>> response =
        controller.handleMethodArgumentNotValidException(exception);

    assertNotNull(response);
    assertEquals(400, response.getStatusCode().value());
    Map<String, Object> body = response.getBody();
    assertNotNull(body);
    assertEquals(400, body.get("code"));
    assertEquals("There are errors that need to be corrected.", body.get("message"));

    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) body.get("errors");
    assertNotNull(errors);
    assertEquals(2, errors.size());
    assertEquals("Name is required", errors.get("name"));
    assertEquals("Status is required", errors.get("status"));
  }

  @Test
  void handleMethodArgumentNotValidExceptionReturnsEmptyErrors() {
    AdviceController controller = new AdviceController();
    BindingResult bindingResult = mock(BindingResult.class);
    MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
    when(exception.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(new ArrayList<>());

    ResponseEntity<Map<String, Object>> response =
        controller.handleMethodArgumentNotValidException(exception);

    assertNotNull(response);
    assertEquals(400, response.getStatusCode().value());
    Map<String, Object> body = response.getBody();

    assertNotNull(body);
    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) body.get("errors");
    assertTrue(errors.isEmpty());
  }


}
