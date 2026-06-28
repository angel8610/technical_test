package com.example.demo.enums;

public enum StatusEnum {

  AVAILABLE("available"),
  PENDING("pending"),
  SOLD("sold");

  private final String value;

  StatusEnum(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }


}
