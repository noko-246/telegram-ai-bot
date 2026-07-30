package org.example.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.example.model.MessageDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MistralClient {
    private static final String API_KEY = "YOUR_API_KEY_MISTRAL";
    private static final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    private static final String MODEL = "mistral-small-latest"; // default free model

    private final HttpClient client;
    private final Gson gson;

    public MistralClient() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public String sendMessageWithHistory(List<MessageDTO> history) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", MODEL);

        JsonArray messagesArray = new JsonArray();
        for (MessageDTO msg : history) {
            JsonObject msgObj = new JsonObject();
            msgObj.addProperty("role", msg.getRole());
            msgObj.addProperty("content", msg.getContent());
            messagesArray.add(msgObj);
        }
        requestBody.add("messages", messagesArray);

        // Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка API: " + response.statusCode() + " - " + response.body());
        }

        // Parsing
        JsonObject jsonResponse = gson.fromJson(response.body(), JsonObject.class);
        String assistantText = jsonResponse
                .getAsJsonArray("choices")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("message")
                .get("content")
                .getAsString();

        history.add(new MessageDTO("assistant", assistantText));

        return assistantText;
    }
}
