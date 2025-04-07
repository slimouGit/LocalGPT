package net.slimou.lmstudio.pdfzusammenfassung;

import com.fasterxml.jackson.databind.JsonNode;
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
public class PdfZusammenfassungController {

    private final String uploadDir = "uploads/";
    PdfZusammenfassungService pdfZusammenfassungService;

    public PdfZusammenfassungController(PdfZusammenfassungService pdfZusammenfassungService) {
        this.pdfZusammenfassungService = pdfZusammenfassungService;
    }

    @GetMapping("/pdf-zusammenfassung")
    public String showPdfZusammenfassungPage(Model model) {
        return "pdf-zusammenfassung";
    }

    @PostMapping("/pdf/upload")
    public String uploadAndAnalyze(@RequestParam("file") MultipartFile file, @RequestParam("searchTerm") String searchTerm, Model model) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            file.transferTo(filePath);
            String pdfText = PdfTextExtractor.extractTextFromPdf(filePath.toString());
            String response = this.pdfZusammenfassungService.getInfoBySearchword(pdfText, searchTerm);
            model.addAttribute("response", response);

            return "pdf-zusammenfassung";
        } catch (IOException e) {
            model.addAttribute("error", "Fehler beim Hochladen: " + e.getMessage());
            return "pdf-zusammenfassung";
        }
    }
}
