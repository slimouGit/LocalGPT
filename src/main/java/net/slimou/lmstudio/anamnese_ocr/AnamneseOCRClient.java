package net.slimou.lmstudio.anamnese_ocr;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class AnamneseOCRClient {
    private final WebClient webClient;

    public AnamneseOCRClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:1234/v1/chat/completions").build();
    }

    public String searchTextWithContext(String pdfText, String query) {
        Map<String, Object> requestBody = Map.of(
                "model", "llama-2-7b-chat",
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