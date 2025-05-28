package net.slimou.lmstudio.arztlichestellungnahme;

import net.slimou.lmstudio.arztlichestellungnahme.helper.ICD10Libary;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ICD10Controller {

    private final ICD10Service gptChatService;
    private ICD10Libary icd10Libary;

    public ICD10Controller(ICD10Service gptChatService) {
        this.gptChatService = gptChatService;
        this.icd10Libary = new ICD10Libary();
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/getICD10Code")
    @ResponseBody
    public String getICD10Code(@RequestParam String description) {
        return this.icd10Libary.icd10Map.entrySet()
                .stream()
                .filter(entry -> entry.getValue().toLowerCase().contains(description.toLowerCase()))
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
    }

    @PostMapping("/sendICDCodes")
    @ResponseBody
    public String sendICDCodes(@RequestBody Map<String, String> request) {
        String diagnose = request.get("diagnose");
        return gptChatService.getCompletion(diagnose);
    }
}