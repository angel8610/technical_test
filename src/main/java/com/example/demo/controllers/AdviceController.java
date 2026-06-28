package com.example.demo.controllers;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class AdviceController {

  @ExceptionHandler(PetNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handlePetNotFound(PetNotFoundException ex) {
    Map<String, Object> body = Map.of("code", HttpStatus.NOT_FOUND.value(),
      "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(body);
  }

  @ExceptionHandler(PetException.class)
  public ResponseEntity<Map<String, Object>> handlePetException(PetException ex) {
    Map<String, Object> body = Map.of("code", HttpStatus.BAD_REQUEST.value(),
      "message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(body);
  }


}
