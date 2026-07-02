package com.example.demo.vos;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionBodyVO {

  private int code;

  private String message;

  private Map<String, String> errors;

  public ExceptionBodyVO(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public ExceptionBodyVO(int code, String message, Map<String, String> errors) {
    this.code = code;
    this.message = message;
    this.errors = errors;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  public void setErrors(Map<String, String> errors) {
    this.errors = errors;
  }


}
