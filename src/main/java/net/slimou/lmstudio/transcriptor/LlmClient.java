package net.slimou.lmstudio.transcriptor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.slimou.lmstudio.Config.API_KEY;

@Service
public class LlmClient {
    private final WebClient webClient;
    private final WebClient openAiClient;

    public LlmClient(WebClient.Builder webClientBuilder) {
        // WebClient für OpenAI Whisper (Transkription)
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com").build();

        // WebClient für OpenAI GPT-Modelle (Summarization)
        this.openAiClient = webClientBuilder.baseUrl("https://api.openai.com").build(); // ❗ KORRIGIERTE BASE URL
    }

    /**
     * Erstellt ein Transkript aus einer Audiodatei mit OpenAI Whisper
     */
    public String generateTranscript(String videoFilePath) {
        FileSystemResource fileResource = new FileSystemResource(new File(videoFilePath));

        // MultiValueMap für multipart/form-data
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", "whisper-1");
        body.add("file", fileResource);

        return webClient.post()
                .uri("/v1/audio/transcriptions") // ✅ KORREKTE API-ROUTE
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + API_KEY)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Erstellt eine Zusammenfassung des Transkripts mit OpenAI GPT-4o
     */
    public String generateSummary(String transcript) {
        // Erstelle die Nachrichtenstruktur für OpenAI Chat API
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "You are an AI assistant that summarizes transcripts."),
                Map.of("role", "user", "content", "Summarize the following transcript:\n" + transcript)
        );

        // Request-Body erstellen
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o"); // ✅ GPT-4o verwenden
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 150);
        requestBody.put("temperature", 0.7);

        return openAiClient.post()
                .uri("/v1/chat/completions") // ✅ KORREKTE API-ROUTE
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + API_KEY)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
