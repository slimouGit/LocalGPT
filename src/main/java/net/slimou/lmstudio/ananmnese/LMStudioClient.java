package net.slimou.lmstudio.ananmnese;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class LMStudioClient {
    private final WebClient webClient;

    public LMStudioClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:1234/v1/chat/completions").build();
    }

    public String searchTextInPdf(String pdfText, String query) {
        Map<String, Object> requestBody = Map.of(
                "model", "meta-llama-3.1-8b-instruct",
                "messages", List.of(
                        Map.of("role", "system", "content", "Du bist ein Dokumentenanalyst."),
                        Map.of("role", "user", "content", "Finde alle Vorkommen von: '" + query + "'\n" + pdfText)
                ),
                "temperature", 0.3
        );

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}