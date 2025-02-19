package com.bsl.wrapper.service;

import com.bsl.wrapper.config.AppConfig;
import com.bsl.wrapper.entity.DallEImageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Service
public class DallEApiClient {
  private final OkHttpClient client;
  private final AppConfig appConfig;
  private final ObjectMapper objectMapper;

  public DallEApiClient(OkHttpClient client, AppConfig appConfig, ObjectMapper objectMapper) {
    this.client = client;
    this.appConfig = appConfig;
    this.objectMapper = objectMapper;
  }

  public DallEImageResponse getImageForPrompt(String prompt) throws IOException {
    String jsonRequest =
        "{"
            + "\"model\": \"dall-e-3\","
            + "\"prompt\": \""
            + prompt
            + "\","
            + "\"n\": 1,"
            + "\"size\": \"1024x1024\""
            + "}";

    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(jsonRequest, mediaType);

    Request request =
        new Request.Builder()
            .url(appConfig.getApiUrlForDalle())
            .post(body)
            .addHeader("Authorization", "Bearer " + appConfig.getOpenaiApiKey())
            .addHeader("Content-Type", "application/json")
            .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        throw new IOException("Error: " + response.code() + " - " + response.message());
      }

      String responseBody = response.body() != null ? response.body().string() : "";
      return objectMapper.readValue(responseBody, DallEImageResponse.class);
    }
  }
}
