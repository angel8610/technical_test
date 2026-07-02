package com.example.demo.controllers;

import com.example.demo.exceptions.PetException;
import com.example.demo.exceptions.PetExistException;
import com.example.demo.exceptions.PetNotFoundException;
import com.example.demo.vos.ExceptionBodyVO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AdviceController {

  @ExceptionHandler(PetNotFoundException.class)
  public ResponseEntity<ExceptionBodyVO> handlePetNotFound(PetNotFoundException ex) {
    var body = new ExceptionBodyVO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
  }

  @ExceptionHandler(PetException.class)
  public ResponseEntity<ExceptionBodyVO> handlePetException(PetException ex) {
    var body = new ExceptionBodyVO(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(body);
  }

  @ExceptionHandler(PetExistException.class)
  public ResponseEntity<ExceptionBodyVO> handlePetExistException(
      PetExistException ex) {
    var body = new ExceptionBodyVO(HttpStatus.CONFLICT.value(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(body);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionBodyVO> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
      errors.put(error.getField(), error.getDefaultMessage())
    );

    var body = new ExceptionBodyVO(HttpStatus.BAD_REQUEST.value(),
      "There are errors that need to be corrected.", errors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(body);
  }


}
