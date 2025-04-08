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
    private static final String BASE_URL = "http://localhost:1234/v1/chat/completions";
    private static final String SYSTEM_PROMPT = "Du bist ein medizinischer Dokumentenanalyst.";
    private static final double TEMPERATURE = 0.2;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public PdfZusammenfassungService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl(BASE_URL).build();
        this.objectMapper = objectMapper;
    }

    public String getInfoBySearchword(String pdfText, String searchTerm) {
        try {
            Map<String, Object> requestBody = createRequestBody(pdfText, searchTerm);
            String response = webClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extractContentFromResponse(response);
        } catch (Exception e) {
            return "Error processing request: " + e.getMessage();
        }
    }

    private Map<String, Object> createRequestBody(String pdfText, String searchTerm) {
        String userPrompt = String.format(
                "Du bist ein Assistent des Aerztlichen Dienstes der Bundesagentur fuer Arbeit. Du Unterstuetzt die Amtsaerzte bei der Erstellung von Gutachten fuer die Kunden des Arbeitsamtes. " +
                        "Extrahiere präzise und relevante Informationen zu: '%s' aus dem Text: %s. " +
                        "Erstelle eine maximal 200 Zeichen lange Zusammenfassung, die ausschließlich den Begriff '%s' behandelt. " +
                        "Die Zusammenfassung darf keine personenbezogenen Daten wie Namen, Geburtsdaten oder Adressen enthalten. " +
                        "Ersetze Namen geschlechtsabhängig durch 'Der Kunde' oder 'Die Kundin'. " +
                        "Vermeide persönliche Kommentare, Interpretationen und allgemeine Formulierungen wie 'Der Anamnesebogen des Patienten'. " +
                        "Fokussiere dich ausschließlich auf die Fakten.",
                searchTerm, pdfText, searchTerm
        );

        return Map.of(
                "model", MODEL_NAME,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", TEMPERATURE
        );
    }

    private String extractContentFromResponse(String responseBody) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "Error: Empty or null response from the API.";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }
    }
}