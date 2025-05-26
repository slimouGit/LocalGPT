package net.slimou.lmstudio.transcriptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.slimou.lmstudio.Config.API_KEY;

@Service
public class TranscriptorService {

    private final Path uploadDir = Paths.get("uploads");
    private final WebClient webClient;

    @Autowired
    public TranscriptorService(WebClient.Builder webClientBuilder) throws IOException {
        Files.createDirectories(uploadDir);
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com").build();
    }

    public String saveFile(MultipartFile file) throws IOException {
        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }

    public String generateTranscript(String videoFilePath) {
        FileSystemResource fileResource = new FileSystemResource(new File(videoFilePath));

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", "whisper-1");
        body.add("file", fileResource);

        return webClient.post()
                .uri("/v1/audio/transcriptions")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header("Authorization", "Bearer " + API_KEY)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String generateSummary(String transcript) {
        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", "You are an AI assistant that summarizes transcripts."),
                Map.of("role", "user", "content", "Summarize the following transcript:\n" + transcript + " Use same language, as the transcript is written in.")
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o");
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);

        String response = webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + API_KEY)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.at("/choices/0/message/content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }
}