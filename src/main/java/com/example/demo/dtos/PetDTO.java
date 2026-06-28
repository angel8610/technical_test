package com.example.demo.dtos;

public class PetDTO {

  private Long id;

  private String name;

  private String status;

  public PetDTO() {
  }

  public PetDTO(Long id, String name, String status) {
    this.id = id;
    this.name = name;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  @Override
  public String toString() {
    return "PetDTO{" +
            "id:" + id +
            ", name:'" + name + '\'' +
            ", status:'" + status + '\'' +
            '}';
  }


}
