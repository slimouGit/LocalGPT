package net.slimou.lmstudio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class GPTChatController {

    private final GPTChatService gptChatService;

    public GPTChatController(GPTChatService gptChatService) {
        this.gptChatService = gptChatService;
    }

    @GetMapping("/")
    public String index(Model model) {
        String prompt = "Willkommen auf der Startseite!";
        String completion = gptChatService.getCompletion(prompt);
        model.addAttribute("message", completion);
        return "index";
    }
}
