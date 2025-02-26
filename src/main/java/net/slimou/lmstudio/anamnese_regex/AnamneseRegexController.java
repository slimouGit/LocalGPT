package net.slimou.lmstudio.anamnese_regex;

import net.slimou.lmstudio.ananmnese.AnamneseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class AnamneseRegexController {
    private final AnamneseRegexService anamneseService;
    private final String uploadDir = "uploads/";

    public AnamneseRegexController(AnamneseRegexService anamneseService) {
        this.anamneseService = anamneseService;
    }

    @GetMapping("/anamnese-regex")
    public String anamnese(Model model) {
        return "anamnese-regex";
    }

    @PostMapping("/anamnese-regex/upload")
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
            String result = AnamneseService.extractRelevantSection(pdfText, searchTerm);

            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Fehler beim Hochladen: " + e.getMessage());
        }
    }
}