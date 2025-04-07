package net.slimou.lmstudio.watsonx;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class WatsonXService {

    @Value("${watsonx.api-key}")
    private String apiKey;

    @Value("${watsonx.base-url}")
    private String baseUrl;

    @Value("${watsonx.project-id}")
    private String projectId;

    @Value("${watsonx.model-id}")
    private String modelId;

    private final WebClient webClient = WebClient.builder().build();

    public String getWatsonxResponse(String userInput) {
        try {
            String token = fetchIamToken();

            Map<String, Object> requestBody = Map.of(
                    "model_id", modelId,
                    "input", userInput,
                    "parameters", Map.of(
                            "decoding_method", "greedy",
                            "max_new_tokens", 200
                    ),
                    "project_id", projectId
            );

            return webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("eu-de.ml.cloud.ibm.com")
                            .path("/ml/v1/text/generation")
                            .queryParam("version", "2023-05-29")
                            .build())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(json -> json.at("/results/0/generated_text").asText())
                    .block();

        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler bei der Anfrage: " + e.getMessage();
        }
    }

    private String fetchIamToken() {
        return webClient.post()
                .uri("https://iam.cloud.ibm.com/identity/token")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue("grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=" + apiKey)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("access_token").asText())
                .block();
    }
}
