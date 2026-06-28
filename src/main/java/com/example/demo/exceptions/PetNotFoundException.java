package com.example.demo.exceptions;

public class PetNotFoundException extends RuntimeException {

  public PetNotFoundException(String message) {
    super(message);
  }


}
