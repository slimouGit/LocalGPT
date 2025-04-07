package net.slimou.lmstudio.pdfzusammenfassung;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

import static net.slimou.lmstudio.Config.MODEL_NAME;


@Service
public class PdfZusammenfassungService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PdfZusammenfassungService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:1234/v1/chat/completions").build();
        this.objectMapper = objectMapper;
    }

    public String getInfoBySearchword(String pdfText, String searchTerm) {
        Map<String, Object> requestBody = Map.of(
                "model", MODEL_NAME,
                "messages", List.of(
                        Map.of("role", "system", "content", "Du bist ein medizinischer Dokumentenanalyst."),
                        Map.of("role", "user", "content", "Finde nur die relevanten Informationen zu: '" + searchTerm + " also die Informationen, die sich direkt auf " + searchTerm + " beziehen in '\n" + pdfText + " Erfasse nur die Daten aus dem Dokument und verzichte auf eigene Kommentare und Interprätationen. Verzichte auch auf Angaben zur Person.")
                ),
                "temperature", 0.2
        );

        String response =  webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractContentFromResponse(response);
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
