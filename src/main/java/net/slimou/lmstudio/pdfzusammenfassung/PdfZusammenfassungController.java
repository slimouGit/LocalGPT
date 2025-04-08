package net.slimou.lmstudio.pdfzusammenfassung;

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

    private static final String UPLOAD_DIR = "uploads/";
    private final PdfZusammenfassungService pdfZusammenfassungService;

    public PdfZusammenfassungController(final PdfZusammenfassungService pdfZusammenfassungService) {
        this.pdfZusammenfassungService = pdfZusammenfassungService;
    }

    @GetMapping("/pdf-zusammenfassung")
    public String showPdfZusammenfassungPage() {
        return "pdf-zusammenfassung";
    }

    @PostMapping("/pdf/upload")
    public String uploadAndAnalyze(@RequestParam("file") MultipartFile file,
                                   @RequestParam("searchTerm") String searchTerm,
                                   Model model) {
        try {
            Path filePath = saveUploadedFile(file);
            String pdfText = PdfTextExtractor.extractTextFromPdf(filePath.toString());
            String response = pdfZusammenfassungService.getInfoBySearchword(pdfText, searchTerm);

            model.addAttribute("response", response);
            model.addAttribute("searchTerm", searchTerm);
        } catch (IOException e) {
            model.addAttribute("error", "File upload error: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred: " + e.getMessage());
        }
        return "pdf-zusammenfassung";
    }

    private Path saveUploadedFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(file.getOriginalFilename());
        file.transferTo(filePath);
        return filePath;
    }
}