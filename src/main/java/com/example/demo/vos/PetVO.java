package com.example.demo.vos;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class PetVO {

  private Long id;

  private CategoryVO category;

  private String name;

  private List<String> photoUrls;

  private List<TagVO> tags;

  private String status;

  public PetVO() {

  }

  public PetVO(Long id, CategoryVO category, String name, List<String> photoUrls,
               List<TagVO> tags, String status) {
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

  public List<String> getPhotoUrls() {
    return photoUrls;
  }

  public void setPhotoUrls(List<String> photoUrls) {
    this.photoUrls = photoUrls;
  }

  public List<TagVO> getTags() {
    return tags;
  }

  public void setTags(List<TagVO> tags) {
    this.tags = tags;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }




}
