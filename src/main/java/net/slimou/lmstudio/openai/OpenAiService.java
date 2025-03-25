package net.slimou.lmstudio.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient webClient;
    private final String model;
    private final String systemprompt;

    public OpenAiService(@Value("${openai.api-key}") String apiKey,
                         @Value("${openai.base-url}") String baseUrl,
                         @Value("${openai.model}") String model,
                         @Value("${openai.systemprompt}") String systemprompt) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.model = model;
        this.systemprompt = systemprompt;
    }

    public String getChatResponse(String userMessage) {
        Map<String, Object> requestBody = Map.of(
                "model", this.model,
                "messages", List.of(
                        Map.of("role", "system", "content", this.systemprompt),
                        Map.of("role", "user", "content", userMessage)
                )
        );

        try {
            String response = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            return root.at("/choices/0/message/content").asText();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler bei OpenAI-Anfrage", e);
        }
    }
}
