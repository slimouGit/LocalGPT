package net.slimou.lmstudio.ananmnese;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AnamneseController {
    private final LMStudioClient lmStudioClient;
    private final String uploadDir = "uploads/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnamneseController(LMStudioClient lmStudioClient) {
        this.lmStudioClient = lmStudioClient;
    }

    @GetMapping("/anamnese")
    public String anamnese() {
        // Prüfen, ob die HTML-Datei vorhanden ist
        Path templatePath = Paths.get("src/main/resources/templates/anamnese.html");
        if (!Files.exists(templatePath)) {
            throw new RuntimeException("Die Datei anamnese.html wurde nicht gefunden. Bitte erstellen Sie sie unter src/main/resources/templates/");
        }
        return "anamnese";
    }

    @PostMapping("/anamnese/upload")
    @ResponseBody
    public ResponseEntity<String> uploadAndAnalyze(@RequestParam("file") MultipartFile file, @RequestParam("searchTerm") String searchTerm) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            file.transferTo(filePath);

            String pdfText = PdfTextExtractor.extractTextFromPdf(filePath.toString());
            String modelResponse = lmStudioClient.searchTextWithContext(pdfText, searchTerm);

            JsonNode jsonNode = objectMapper.readTree(modelResponse);
            String modelContent = jsonNode.path("choices").get(0).path("message").path("content").asText();

            return ResponseEntity.ok(modelContent);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fehler beim Hochladen: " + e.getMessage());
        }
    }
}