package net.slimou.lmstudio.ananmnese;

import net.slimou.lmstudio.anamnese_regex.PdfTextExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AnamneseController {
    private final AnamneseClient lmStudioClient;
    private final String uploadDir = "uploads/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnamneseController(AnamneseClient lmStudioClient) {
        this.lmStudioClient = lmStudioClient;
    }

    @GetMapping("/anamnese")
    public String anamnese(Model model) {
        // Prüfen, ob die HTML-Datei vorhanden ist
        Path templatePath = Paths.get("src/main/resources/templates/anamnese.html");
        if (!Files.exists(templatePath)) {
            throw new RuntimeException("Die Datei anamnese.html wurde nicht gefunden. Bitte erstellen Sie sie unter src/main/resources/templates/");
        }
        return "anamnese";
    }

    @PostMapping("/anamnese/upload")
    public String uploadAndAnalyze(@RequestParam("file") MultipartFile file, @RequestParam("searchTerm") String searchTerm, Model model) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            file.transferTo(filePath);

            String pdfText = PdfTextExtractor.extractTextFromPdf(filePath.toString().getBytes());
            String modelResponse = lmStudioClient.searchTextWithContext(pdfText, searchTerm);

            JsonNode jsonNode = objectMapper.readTree(modelResponse);
            String modelContent = jsonNode.path("choices").get(0).path("message").path("content").asText();

            model.addAttribute("analysisResult", modelContent);
            return "anamnese";
        } catch (IOException e) {
            model.addAttribute("error", "Fehler beim Hochladen: " + e.getMessage());
            return "anamnese";
        }
    }
}