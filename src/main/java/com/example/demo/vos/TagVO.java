package com.example.demo.vos;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class TagVO {

  private Long id;

  private String name;

  public TagVO() {

  }

  public TagVO(Long id, String name) {
    if(StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("The tag name cannot be blank.");
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

    TagVO tagVO = (TagVO) o;
    return id.equals(tagVO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }



}
