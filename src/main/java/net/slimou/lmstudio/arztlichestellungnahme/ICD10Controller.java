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

    private final ICD10Service gptChatService;
    private ICD10Libary icd10Libary;

    public ICD10Controller(ICD10Service gptChatService) {
        this.gptChatService = gptChatService;
        this.icd10Libary = new ICD10Libary();
    }


    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("chatForm", new ChatForm());
        return "index";
    }

    @PostMapping("/send")
    public String send(@ModelAttribute ChatForm chatForm, Model model) {
        String completion = gptChatService.getCompletion(chatForm.getPrompt());
        model.addAttribute("response", completion);
        model.addAttribute("chatForm", chatForm);
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
        return gptChatService.getCompletion(Collections.singletonList(diagnose));
    }
}