package net.slimou.lmstudio.ananmnese;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import java.util.*;

import static net.slimou.lmstudio.Config.MODEL_NAME;

@Service
public class AnamneseClient {
    private final WebClient webClient;

    public AnamneseClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:1234/v1/chat/completions").build();
    }

    public String searchTextWithContext(String pdfText, String query) {
        Map<String, Object> requestBody = Map.of(
                "model", MODEL_NAME,
                "messages", List.of(
                        Map.of("role", "system", "content", "Du bist ein medizinischer Dokumentenanalyst."),
                        Map.of("role", "user", "content", "Finde nur die relevanten Informationen zu: '" + query + " also die Informationen, die sich direkt auf " + query + " beziehen '\n" + pdfText + " Erfasse nur die Daten aus dem Dokument und verzichte auf eigene Kommentare und Interprätationen. Verzichte auch auf Angaben zur Person.")
                ),
                "temperature", 0.2
        );

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}