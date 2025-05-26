package net.slimou.lmstudio.zusammenfassung;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ZusammenfassungController {

    private ZusammenfassungService zusammenfassungService;

    public ZusammenfassungController(ZusammenfassungService zusammenfassungService) {
        this.zusammenfassungService = zusammenfassungService;
    }

    @GetMapping("/zusammenfassung")
    public String zusammenfassung(Model model) {
        // Prüfen, ob die HTML-Datei vorhanden ist
        Path templatePath = Paths.get("src/main/resources/templates/keywordinfo.html");
        if (!Files.exists(templatePath)) {
            throw new RuntimeException("Die Datei keywordinfo.html wurde nicht gefunden. Bitte erstellen Sie sie unter src/main/resources/templates/");
        }

        return "keywordinfo";
    }

    @GetMapping("/zusammenfassung/generate")
    public String generateZusammenfassung(Model model) {
        String zusammenfassung = zusammenfassungService.getZusammenfassung();
        model.addAttribute("zusammenfassung", zusammenfassung);
        return "keywordinfo";
    }

    @PostMapping("/zusammenfassung/search")
    public String searchKeyword(@RequestParam("keyword") String keyword, @RequestParam("source") String source, Model model) {
        String response = zusammenfassungService.getDataForKeyword(keyword, source);
        model.addAttribute("response", response);
        return "keywordinfo";
    }
}
