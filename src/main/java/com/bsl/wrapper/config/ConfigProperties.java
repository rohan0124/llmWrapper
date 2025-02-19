package com.bsl.wrapper.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigProperties {

  private String googleApiUrl;
  private String geminiModel;
  private String geminiApiKey;
  private String dallEApiUrl;
  private String openaiApiKey;

  // Getters and Setters
  public String getGoogleApiUrl() {
    return googleApiUrl;
  }

  public void setGoogleApiUrl(String googleApiUrl) {
    this.googleApiUrl = googleApiUrl;
  }

  public String getGeminiModel() {
    return geminiModel;
  }

  public void setGeminiModel(String geminiModel) {
    this.geminiModel = geminiModel;
  }

  public String getGeminiApiKey() {
    return geminiApiKey;
  }

  public void setGeminiApiKey(String geminiApiKey) {
    this.geminiApiKey = geminiApiKey;
  }

  public String getDallEApiUrl() {
    return dallEApiUrl;
  }

  public void setDallEApiUrl(String dallEApiUrl) {
    this.dallEApiUrl = dallEApiUrl;
  }

  public String getOpenaiApiKey() {
    return openaiApiKey;
  }

  public void setOpenaiApiKey(String openaiApiKey) {
    this.openaiApiKey = openaiApiKey;
  }
}
