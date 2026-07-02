package com.example.demo.vos;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }

    if(o == null || getClass() != o.getClass()) {
      return false;
    }

    CategoryVO categoryVO = (CategoryVO) o;
    return id.equals(categoryVO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }


}
