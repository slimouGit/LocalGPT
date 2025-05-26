package net.slimou.lmstudio.arztlichestellungnahme;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static net.slimou.lmstudio.Config.*;

@Service
public class ICD10Service {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public ICD10Service(RestTemplate restTemplate, ObjectMapper objectMapper, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com").build();
    }

    public String getCompletion(List<String> selectedIcds) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String systemPrompt = PromptGenerator.generateSystemPrompt(selectedIcds);
        String userPrompt = PromptGenerator.generateUserPrompt(selectedIcds);

        String requestBody = "{\"model\": \"" + MODEL_NAME + "\", \"messages\": [" +
                "{\"role\": \"system\", \"content\": \""+ systemPrompt +"\"}," +
                "{\"role\": \"user\", \"content\": \"" + userPrompt + "\"}]," +
                "\"max_tokens\": " + MAX_TOKENS + ", \"temperature\": " + TEMPERATURE + "}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(LOCAL_API_URL, HttpMethod.POST, entity, String.class);
        return extractContentFromResponse(response.getBody());
    }

    public String getCompletionOpenAI(List<String> selectedIcds) {


        String prompt = PromptGenerator.generateSystemPrompt(selectedIcds);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4o",
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", prompt)
                ),
                "max_tokens", MAX_TOKENS,
                "temperature", TEMPERATURE
        );

        try {
            String response = webClient.post()
                    .uri("/v1/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + API_KEY)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.at("/choices/0/message/content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error processing OpenAI API request: " + e.getMessage();
        }
    }

    private String extractContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }
}