package net.slimou.lmstudio;

import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ICD10Controller {

    private static final Map<String, String> icd10Map = new HashMap<>();
    private final GPTChatService gptChatService;

    public ICD10Controller(GPTChatService gptChatService) {
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

    @GetMapping("/getICD10Code")
    public String getICD10Code(@RequestParam String description) {
        return icd10Map.entrySet()
                .stream()
                .filter(entry -> entry.getValue().toLowerCase().contains(description.toLowerCase()))
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    @PostMapping("/sendICDCodes")
    public String sendICDCodes(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        return gptChatService.getCompletion(Collections.singletonList(prompt), "");
    }
}