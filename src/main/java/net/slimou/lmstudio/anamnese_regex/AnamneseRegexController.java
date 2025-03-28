package net.slimou.lmstudio.anamnese_regex;

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
    public String uploadAndAnalyze(@RequestParam("file") MultipartFile file, @RequestParam("searchTerm") String searchTerm, Model model) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            file.transferTo(filePath);

            String pdfText = PdfTextExtractor.extractTextFromPdf(filePath.toString());
            String result = AnamneseRegexService.extractRelevantSection(pdfText, searchTerm);

            model.addAttribute("analysisResult", result);
            return "anamneseauswertung-regex";
        } catch (IOException e) {
            model.addAttribute("error", "Fehler beim Hochladen: " + e.getMessage());
            return "anamnese-regex";
        }
    }
}