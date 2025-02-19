package com.bsl.wrapper.service;

import com.bsl.wrapper.dao.DaoHelper;
import com.bsl.wrapper.entity.DallEImageResponse;
import com.bsl.wrapper.entity.GeminiResponse;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ApiService {
  private final ExecutorService textExecutorService;
  private final ExecutorService mutlimodalExecutorService;
  private final ExecutorService imageGeneratorService;
  private final GeminiApiClient geminiApiClient;
  private final DallEApiClient dalleApiClient;
  private final DaoHelper daoHelper;

  public ApiService(
      GeminiApiClient geminiApiClient, DallEApiClient dalleApiClient, DaoHelper daoHelper) {
    this.geminiApiClient = geminiApiClient;
    this.dalleApiClient = dalleApiClient;
    this.daoHelper = daoHelper;
    this.textExecutorService = Executors.newFixedThreadPool(5);
    this.mutlimodalExecutorService = Executors.newFixedThreadPool(2);
    this.imageGeneratorService = Executors.newFixedThreadPool(2);
    // Thread pool with 10 threads
  }

  public Future<GeminiResponse> fetchGeneratedText(String prompt) {
    return textExecutorService.submit(
        () -> {
          GeminiResponse response = geminiApiClient.generateText(prompt);
          daoHelper.saveRequestResponse(
              prompt, response.getText(), Type.TEXT_TEXT.name()); // Store in file
          return response;
        });
  }

  public Future<GeminiResponse> fetchGeneratedTextForImage(MultipartFile file, String prompt) {
    return mutlimodalExecutorService.submit(
        () -> {
          String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
          GeminiResponse response = geminiApiClient.getReplyForImage(base64Image, prompt);
          daoHelper.saveRequestResponse(
              prompt + getFileInfo(file),
              response.getText(),
              Type.MULTIMODAL_TEXT.name()); // Store in file
          return response;
        });
  }

  public Future<DallEImageResponse> fetchGeneratedImageForText(String prompt) {
    return imageGeneratorService.submit(
        () -> {
          DallEImageResponse response = dalleApiClient.getImageForPrompt(prompt);
          daoHelper.saveRequestResponse(prompt, response.getImageUrl(), Type.TEXT_IMAGE.name());
          return response;
        });
  }

  private String getFileInfo(MultipartFile file) {
    String fileName = file.getOriginalFilename();
    return " File:: type: "
        + file.getContentType()
        + " name: "
        + fileName
        + " size: "
        + file.getSize();
  }

  enum Type {
    TEXT_TEXT,
    MULTIMODAL_TEXT,
    TEXT_IMAGE,
    MULTIMODAL_IMAGE,
  }
}
