package com.bsl.wrapper.entity;

import java.util.List;

public class DallEImageResponse {
  private long created;
  private List<ImageData> data;

  public String getImageUrl() {
    return data.get(0).getUrl();
  }
  // Inner class to represent image data
  public static class ImageData {
    private String url;

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }

  // Getters and Setters
  public long getCreated() {
    return created;
  }

  public void setCreated(long created) {
    this.created = created;
  }

  public List<ImageData> getData() {
    return data;
  }

  public void setData(List<ImageData> data) {
    this.data = data;
  }
}
