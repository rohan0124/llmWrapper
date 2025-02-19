package com.bsl.wrapper.dao;

import io.micrometer.common.util.StringUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

@Repository
public class FileDaoHelper implements DaoHelper {

  private static final String FILE_PATH = "requests_responses.txt";

  @Override
  public synchronized void saveRequestResponse(String request, String response, String type) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
      writer.write("Timestamp: " + LocalDateTime.now() + " Type: " + type + "\n");
      writer.write("Prompt: " + request + "\n");
      writer.write("Response: " + StringUtils.truncate(response.strip(), 400, "....") + "\n");
      writer.write("------------------------\n");
    } catch (IOException e) {
      e.printStackTrace(); // Replace with proper logging
      throw new RuntimeException(e);
    }
  }
}
