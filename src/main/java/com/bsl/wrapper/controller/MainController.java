package com.bsl.wrapper.controller;

import com.bsl.wrapper.entity.DallEImageResponse;
import com.bsl.wrapper.entity.GeminiResponse;
import com.bsl.wrapper.service.ApiService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("/bsl/v1")
public class MainController {

  private static final long MAX_IMAGE_FILE_SIZE = 12 * 1024 * 1024; // 10mb
  private static final List<String> ALLOWED_IMAGE_MIME_TYPES =
      Arrays.asList("image/png", "image/jpeg", "image/webp", "image/heic", "image/heif");

  @Autowired private ApiService apiService;

  @PostMapping("/text/getReply")
  public ResponseEntity<?> getReplyForText(@RequestParam String prompt) {
    validatePrompt(prompt);
    return processApiCall(() -> apiService.fetchGeneratedText(prompt));
  }

  @PostMapping("/image/getReply")
  public ResponseEntity<?> getReplyForImage(
      @RequestParam("prompt") String prompt, @RequestParam("image") MultipartFile file) {

    if (file.getSize() > MAX_IMAGE_FILE_SIZE) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(Collections.singletonMap("error", "File size exceeds 10MB limit"));
    }
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_IMAGE_MIME_TYPES.contains(contentType.toLowerCase())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(
              Collections.singletonMap(
                  "error", "Invalid file type. Allowed: PNG, JPEG, WEBP, HEIC, HEIF"));
    }
    return processApiCall(() -> apiService.fetchGeneratedTextForImage(file, prompt));
  }

  @PostMapping("/text/getImage")
  public ResponseEntity<?> getImageForText(@RequestParam("prompt") String prompt) {
    validatePrompt(prompt);
    return processImageApiCall(() -> apiService.fetchGeneratedImageForText(prompt));
  }

  @PostMapping("/pdf/getReply")
  public String getReplyForPdf() {
    return "Hello World";
  }

  @PostMapping("/image/getImage")
  public String getImageForImage() {
    return "Hello World";
  }

  @PostMapping("/pdf/getImage")
  public String getImageForPdf() {
    return "Hello World";
  }

  private ResponseEntity<?> processImageApiCall(Callable<Future<DallEImageResponse>> apiCall) {
    try {
      DallEImageResponse response = apiCall.call().get(15, TimeUnit.SECONDS); // Blocking call

      if (response == null || response.getData().isEmpty()) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Collections.singletonMap("error", "Empty response from API"));
      }

      // Extract relevant text from response
      String generatedText = response.getImageUrl();
      return ResponseEntity.ok(Collections.singletonMap("response", generatedText));

    } catch (TimeoutException e) {
      return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
          .body(Collections.singletonMap("error", "Request timed out"));
    } catch (ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", e.getCause().getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", e.getMessage()));
    }
  }

  private ResponseEntity<?> processApiCall(Callable<Future<GeminiResponse>> apiCall) {
    try {
      GeminiResponse response = apiCall.call().get(15, TimeUnit.SECONDS); // Blocking call

      if (response == null || response.getCandidates().isEmpty()) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Collections.singletonMap("error", "Empty response from API"));
      }

      // Extract relevant text from response
      String generatedText = response.getText();
      return ResponseEntity.ok(Collections.singletonMap("response", generatedText));

    } catch (TimeoutException e) {
      return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
          .body(Collections.singletonMap("error", "Request timed out"));
    } catch (ExecutionException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", e.getCause().getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Collections.singletonMap("error", e.getMessage()));
    }
  }

  private void validatePrompt(String prompt) {
    if (prompt == null || prompt.trim().isEmpty()) {
      throw new IllegalArgumentException("Invalid prompt");
    }
  }
}
