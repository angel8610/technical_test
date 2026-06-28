package com.example.demo.dtos;

import java.time.LocalDateTime;

public class PetSaveResponseDTO {

  private String transactionId;

  private LocalDateTime dateCreated;

  private  boolean status;

  public PetSaveResponseDTO() {

  }

  public PetSaveResponseDTO(String transactionId, LocalDateTime dateCreated, boolean status,
                            String name) {
    this.transactionId = transactionId;
    this.dateCreated = dateCreated;
    this.status = status;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isStatus() {
    return status;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(LocalDateTime dateCreated) {
    this.dateCreated = dateCreated;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

  private String name;




}
