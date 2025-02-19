package com.bsl.wrapper.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

  @JsonProperty("candidates")
  private List<Candidate> candidates;

  public List<Candidate> getCandidates() {
    return candidates;
  }

  public String getText() {
    if (candidates != null && !candidates.isEmpty()) {
      Candidate candidate = candidates.get(0); // Assuming we take the first candidate
      if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
        return candidate
            .getContent()
            .getParts()
            .stream()
            .map(Part::getText)
            .reduce("", (a, b) -> a + "\n" + b); // Join all text parts
      }
    }
    return "No response text available.";
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Candidate {
    @JsonProperty("content")
    private Content content;

    public Content getContent() {
      return content;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Content {
    @JsonProperty("parts")
    private List<Part> parts;

    public List<Part> getParts() {
      return parts;
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Part {
    @JsonProperty("text")
    private String text;

    public String getText() {
      return text;
    }
  }
}
