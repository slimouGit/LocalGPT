package net.slimou.lmstudio.transcriptor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TranscriptorService {

    private final Path uploadDir = Paths.get("uploads");

    public TranscriptorService() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public String saveFile(MultipartFile file) throws IOException {
        Path filePath = uploadDir.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), filePath);
        return filePath.toString();
    }
}
