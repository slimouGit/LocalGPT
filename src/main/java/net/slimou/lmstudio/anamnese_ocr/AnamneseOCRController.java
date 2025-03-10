package net.slimou.lmstudio.anamnese_ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.slimou.lmstudio.anamnese_regex.PdfTextExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AnamneseOCRController {
    private final AnamneseOCRClient lmStudioClient;
    private final String uploadDir = "uploads/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnamneseOCRController(AnamneseOCRClient lmStudioClient) {
        this.lmStudioClient = lmStudioClient;
    }

    @GetMapping("/anamnese-ocr")
    public String anamnese(Model model) {
        // Prüfen, ob die HTML-Datei vorhanden ist
        Path templatePath = Paths.get("src/main/resources/templates/anamnese-ocr.html");
        if (!Files.exists(templatePath)) {
            throw new RuntimeException("Die Datei anamnese.html wurde nicht gefunden. Bitte erstellen Sie sie unter src/main/resources/templates/");
        }
        return "anamnese-ocr";
    }

    @PostMapping("/anamnese-ocr/upload")
    public String uploadAndAnalyze(@RequestParam("file") MultipartFile file, @RequestParam("searchTerm") String searchTerm, Model model) {
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

            model.addAttribute("analysisResult", modelContent);
            return "anamnese-ocr";
        } catch (IOException e) {
            model.addAttribute("error", "Fehler beim Hochladen: " + e.getMessage());
            return "anamnese-ocr";
        }
    }
}