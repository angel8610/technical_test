package com.example.demo.vos;

import org.apache.commons.lang3.StringUtils;

public class CategoryVO {

  private Long id;

  private String name;

  public CategoryVO() {

  }

  public CategoryVO(Long id, String name) {
    if(StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("The category name cannot be blank.");
    }

    this.id = id;
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


}
