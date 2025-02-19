package com.bsl.wrapper.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

  private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
  private static final String CONFIG_FILE = "config.json";

  private final ConfigProperties configProperties = new ConfigProperties();

  @PostConstruct
  public void init() {
    try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (inputStream == null) {
        throw new IOException("Configuration file not found: " + CONFIG_FILE);
      }

      ObjectMapper objectMapper = new ObjectMapper();
      ConfigProperties properties = objectMapper.readValue(inputStream, ConfigProperties.class);
      configProperties.setGoogleApiUrl(properties.getGoogleApiUrl());
      configProperties.setGeminiModel(properties.getGeminiModel());
      configProperties.setGeminiApiKey(properties.getGeminiApiKey());
      configProperties.setDallEApiUrl(properties.getDallEApiUrl());
      configProperties.setOpenaiApiKey(properties.getOpenaiApiKey());

      logger.info("Configuration loaded successfully.");
    } catch (IOException e) {
      logger.error("Failed to load API configuration from JSON", e);
      throw new RuntimeException("Failed to load API configuration", e);
    }
  }

  @Bean
  public OkHttpClient okHttpClient() {
    return new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
  }

  public String getApiUrl() {
    return configProperties.getGoogleApiUrl() + configProperties.getGeminiModel() + getGeminiKey();
  }

  public String getApiUrlForTextGemini() {
    return configProperties.getGoogleApiUrl()
        + configProperties.getGeminiModel()
        + "generateContent"
        + getGeminiKey();
  }

  public String getGeminiKey() {
    return "?key=" + configProperties.getGeminiApiKey();
  }

  public String getApiUrlForDalle() {
    return configProperties.getDallEApiUrl();
  }

  public String getOpenaiApiKey() {
    return configProperties.getOpenaiApiKey();
  }
}
