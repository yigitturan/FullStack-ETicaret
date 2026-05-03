package com.yigit.ecommerce.assistant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class OpenAIService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String askGPT(String prompt) throws Exception {

        String apiKey = System.getenv("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("OPENAI_API_KEY bulunamadi");
        }

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4.1-mini",
                "input", prompt
        );

        String jsonBody = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/responses"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("OPENAI RAW RESPONSE: " + response.body()); // DEBUG

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("OpenAI API hata verdi: " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());

        // direkt output_text varsa al
        if (root.has("output_text") && !root.get("output_text").isNull()) {
            return root.get("output_text").asText();
        }

        // output array icinden text'i cek
        if (root.has("output")) {
            for (JsonNode item : root.get("output")) {
                if (item.has("content")) {
                    for (JsonNode content : item.get("content")) {
                        if (content.has("text")) {
                            return content.get("text").asText();
                        }
                    }
                }
            }
        }

        throw new RuntimeException("OpenAI cevabi parse edilemedi: " + response.body());
    }
}