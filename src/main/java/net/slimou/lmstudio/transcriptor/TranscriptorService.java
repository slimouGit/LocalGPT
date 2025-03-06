package net.slimou.lmstudio.transcriptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TranscriptorService {

    private final Path uploadDir = Paths.get("uploads");
    private final LlmClient llmClient;

    @Autowired
    public TranscriptorService(LlmClient llmClient) throws IOException {
        this.llmClient = llmClient;
        Files.createDirectories(uploadDir);
    }

    public String saveFile(MultipartFile file) throws IOException {
        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }

    public String generateTranscript(String videoFilePath) {
        return llmClient.generateTranscript(videoFilePath);
    }
}