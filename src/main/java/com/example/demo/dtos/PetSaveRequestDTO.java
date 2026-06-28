package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PetSaveRequestDTO(

  @NotNull(message = "Id is invalid")
  Long id,

  @NotBlank(message = "Name is invalid")
  String name,

  @NotBlank(message = "Status is invalid")
  String status) {
}
