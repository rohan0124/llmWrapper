package com.bsl.wrapper.service;

import com.bsl.wrapper.config.AppConfig;
import com.bsl.wrapper.entity.GeminiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.StringEscapeUtils;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

@Service
public class GeminiApiClient {

  private final OkHttpClient client;
  private final AppConfig appConfig;
  private final ObjectMapper objectMapper;

  public GeminiApiClient(OkHttpClient client, AppConfig appConfig, ObjectMapper objectMapper) {
    this.client = client;
    this.appConfig = appConfig;
    this.objectMapper = objectMapper;
  }

  public GeminiResponse generateText(String prompt) throws IOException {
    String escapedPrompt = StringEscapeUtils.escapeJson(prompt);
    String jsonRequest = "{\"contents\": [{\"parts\": [{\"text\": \"" + escapedPrompt + "\"}]}]}";

    return getGeminiTextResponse(jsonRequest);
  }

  public GeminiResponse getReplyForImage(String base64Image, String prompt) throws IOException {
    String requestBody = buildJsonPayloadWithImage(base64Image, prompt);

    return getGeminiTextResponse(requestBody);
  }

  private GeminiResponse getGeminiTextResponse(String jsonRequest) throws IOException {
    MediaType mediaType = MediaType.get("application/json; charset=utf-8");
    RequestBody body = RequestBody.create(jsonRequest, mediaType);

    Request request =
        new Request.Builder()
            .url(appConfig.getApiUrlForTextGemini())
            .post(body)
            .header("Content-Type", "application/json")
            .build();

    try (Response response = client.newCall(request).execute()) {
      if (!response.isSuccessful()) {
        String errorBody = response.body() != null ? response.body().string() : "Empty error body";
        throw new IOException(
            "Error: " + response.code() + " - " + response.message() + "\nBody: " + errorBody);
      }

      String responseBody = response.body() != null ? response.body().string() : "";
      return objectMapper.readValue(responseBody, GeminiResponse.class);
    }
  }

  private String buildJsonPayloadWithImage(String base64Image, String prompt) {
    String escapedPrompt = StringEscapeUtils.escapeJson(prompt);
    return "{ \"contents\": [{ \"parts\": ["
        + "{ \"text\": \""
        + escapedPrompt
        + "\" }, "
        + "{ \"inline_data\": { \"mime_type\": \"image/jpeg\", \"data\": \""
        + base64Image
        + "\" } }"
        + "] } ] }";
  }
}
