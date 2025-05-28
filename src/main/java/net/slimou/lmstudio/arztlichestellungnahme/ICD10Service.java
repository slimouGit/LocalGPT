package net.slimou.lmstudio.arztlichestellungnahme;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.slimou.lmstudio.arztlichestellungnahme.helper.PromptGenerator;
import net.slimou.lmstudio.arztlichestellungnahme.helper.TrainingData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.slimou.lmstudio.Config.*;

@Service
public class ICD10Service {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ICD10Service(RestTemplate restTemplate, ObjectMapper objectMapper, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getCompletion(String selectedIcds) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String exampleTexts = getExamples(selectedIcds);
        String systemPrompt = PromptGenerator.generateSystemPrompt(exampleTexts);
        String userPrompt = PromptGenerator.generateUserPrompt(selectedIcds);
        String requestBody = "{\"model\": \"" + MODEL_NAME + "\", \"messages\": [" +
                "{\"role\": \"system\", \"content\": \""+ systemPrompt +"\"}," +
                "{\"role\": \"user\", \"content\": \"" + userPrompt + "\"}]," +
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

    private static List<String> extractICDCodes(String selectedIcds) {
        return Arrays.stream(selectedIcds.split(","))
                .map(entry -> entry.split(":")[0].trim())
                .collect(Collectors.toList());
    }

    private static String getExamples(String selectedIcds) {
        StringBuilder examples = new StringBuilder();
        List<String> icds = extractICDCodes(selectedIcds);
        for(String icd : icds){
            examples.append(TrainingData.getExampleText(icd));
        }
        return examples.toString();
    }
}