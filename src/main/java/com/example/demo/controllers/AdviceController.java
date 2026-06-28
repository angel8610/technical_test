package com.example.demo.controllers;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AdviceController {

  private static final String CODE = "code";
  private static final String MESSAGE = "message";

  @ExceptionHandler(PetNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handlePetNotFound(PetNotFoundException ex) {
    Map<String, Object> body = Map.of(CODE, HttpStatus.NOT_FOUND.value(),
      MESSAGE, ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(body);
  }

  @ExceptionHandler(PetException.class)
  public ResponseEntity<Map<String, Object>> handlePetException(PetException ex) {
    Map<String, Object> body = Map.of(CODE, HttpStatus.INTERNAL_SERVER_ERROR.value(),
      MESSAGE, ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
      errors.put(error.getField(), error.getDefaultMessage())
    );

    Map<String, Object> body = Map.of(CODE, HttpStatus.BAD_REQUEST.value(),
      MESSAGE, "There are errors that need to be corrected.",
      "errors", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(body);
  }


}
