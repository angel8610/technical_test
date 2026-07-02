package com.example.demo.vos;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

public class PetVO {

  private Long id;

  private CategoryVO category;

  private String name;

  private Set<String> photoUrls;

  private Set<TagVO> tags;

  private String status;

  public PetVO() {

  }

  public PetVO(Long id, CategoryVO category, String name, Set<String> photoUrls,
               Set<TagVO> tags, String status) {
    if (StringUtils.isBlank(name)) {
      throw new IllegalArgumentException("The name cannot be blank.");
    }

    if (StringUtils.isBlank(status)) {
      throw new IllegalArgumentException("The status cannot be blank.");
    }

    this.id = id;
    this.category = category;
    this.name = name;
    this.photoUrls = photoUrls;
    this.tags = tags;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public CategoryVO getCategory() {
    return category;
  }

  public void setCategory(CategoryVO category) {
    this.category = category;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getPhotoUrls() {
    return photoUrls;
  }

  public void setPhotoUrls(Set<String> photoUrls) {
    this.photoUrls = photoUrls;
  }

  public Set<TagVO> getTags() {
    return tags;
  }

  public void setTags(Set<TagVO> tags) {
    this.tags = tags;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }

    if(o == null || getClass() != o.getClass()) {
      return false;
    }

    PetVO petVO = (PetVO) o;
    return id.equals(petVO.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }


}
