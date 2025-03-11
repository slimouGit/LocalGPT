package net.slimou.lmstudio.anamnese_ocr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class AnamneseOCRController {
    private final AnamneseOCRService anamneseOCRService;
    private final String uploadDir = "uploads/";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnamneseOCRController(AnamneseOCRService anamneseOCRService) {
        this.anamneseOCRService = anamneseOCRService;
    }

    @GetMapping("/anamnese-ocr")
    public String anamnese(Model model) {
        Path templatePath = Paths.get("src/main/resources/templates/anamnese-ocr.html");
        if (!Files.exists(templatePath)) {
            throw new RuntimeException("Die Datei anamnese.html wurde nicht gefunden. Bitte erstellen Sie sie unter src/main/resources/templates/");
        }
        return "anamnese-ocr";
    }

    @PostMapping("/anamnese-ocr/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            File uploadDir = new File(this.uploadDir);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            Path filePath = Path.of(this.uploadDir + file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            model.addAttribute("message", "Datei erfolgreich hochgeladen: " + file.getOriginalFilename());
        } catch (Exception e) {
            model.addAttribute("message", "Fehler beim Hochladen der Datei.");
        }
        return "anamnese-ocr";
    }

    @PostMapping("/anamnese-ocr/search")
    public ResponseEntity<List<String>> search(@RequestParam("keyword") String keyword) {
        List<String> results = anamneseOCRService.searchInPDFs(uploadDir, keyword);
        return ResponseEntity.ok(results);
    }
}
