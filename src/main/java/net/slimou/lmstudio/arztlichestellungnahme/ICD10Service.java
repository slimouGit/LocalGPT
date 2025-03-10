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

import java.util.List;

import static net.slimou.lmstudio.Config.*;

@Service
public class ICD10Service {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ICD10Service(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getCompletion(List<String> selectedIcds, String enteredDiagnosis) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = PromptGenerator.generatePrompt(selectedIcds, enteredDiagnosis);

        String requestBody = "{\"model\": \"" + MODEL_NAME + "\", \"messages\": [" +
                "{\"role\": \"system\", \"content\": \""+ SYSTEM_PROMPT +"\"}," +
                "{\"role\": \"user\", \"content\": \"" + prompt + "\"}]," +
                "\"max_tokens\": " + MAX_TOKENS + ", \"temperature\": " + TEMPERATURE + "}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(LOCAL_API_URL, HttpMethod.POST, entity, String.class);
        return extractContentFromResponse(response.getBody());
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