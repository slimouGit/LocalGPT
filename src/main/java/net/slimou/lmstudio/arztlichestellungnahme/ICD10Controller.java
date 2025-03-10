package net.slimou.lmstudio.arztlichestellungnahme;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ICD10Controller {

    private static final Map<String, String> icd10Map = new HashMap<>();
    private final ICD10Service gptChatService;

    public ICD10Controller(ICD10Service gptChatService) {
        this.gptChatService = gptChatService;
    }

    static {
        icd10Map.put("F32.0", "Leichte depressive Episode");
        icd10Map.put("J45.9", "Asthma, nicht näher bezeichnet");
        icd10Map.put("E11.9", "Typ-2-Diabetes mellitus ohne Komplikationen");
        icd10Map.put("I10", "Essentielle Hypertonie");
        icd10Map.put("M54.5", "Kreuzschmerz");
        icd10Map.put("N39.0", "Harnwegsinfektion, nicht näher bezeichnet");
        icd10Map.put("K52.9", "Nichtinfektiöse Gastroenteritis und Kolitis, nicht näher bezeichnet");
        icd10Map.put("L20.9", "Atopische Dermatitis");
        icd10Map.put("G40.9", "Epilepsie, nicht näher bezeichnet");
        icd10Map.put("C34.9", "Bösartige Neubildung der Bronchien oder der Lunge");
        icd10Map.put("F13.3", "Abhängigkeitssyndrom durch Sedativa oder Hypnotika");
        icd10Map.put("M54.6", "Schmerzen im unteren Rückenbereich");
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("chatForm", new ChatForm());
        return "index";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute ChatForm chatForm, Model model) {
        String completion = gptChatService.getCompletion(chatForm.getPrompt(), "");
        model.addAttribute("response", completion);
        model.addAttribute("chatForm", chatForm);
        return "index";
    }

    @GetMapping("/getICD10Code")
    @ResponseBody
    public String getICD10Code(@RequestParam String description) {
        return icd10Map.entrySet()
                .stream()
                .filter(entry -> entry.getValue().toLowerCase().contains(description.toLowerCase()))
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    @PostMapping("/sendICDCodes")
    @ResponseBody
    public String sendICDCodes(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        int maxContextLength = 1000; // Model's context length

        if (prompt.length() > maxContextLength) {
            return "Error: Input prompt is too long. Please provide a shorter input.";
        }

        return gptChatService.getCompletion(Collections.singletonList(prompt), "");
    }
}