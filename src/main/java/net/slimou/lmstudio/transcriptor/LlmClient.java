package net.slimou.lmstudio.transcriptor;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.util.List;
import java.util.Map;

import static net.slimou.lmstudio.Config.API_KEY;

@Service
public class LlmClient {
    private final WebClient webClient;

    public LlmClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/audio/transcriptions").build();
    }

    public String generateTranscript(String videoFilePath) {
        FileSystemResource fileResource = new FileSystemResource(new File(videoFilePath));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", "whisper-1");
        body.add("file", fileResource);

        return webClient.post()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + API_KEY)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}