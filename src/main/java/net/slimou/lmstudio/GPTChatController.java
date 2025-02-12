package net.slimou.lmstudio;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class GPTChatController {

    private final GPTChatService gptChatService;

    public GPTChatController(GPTChatService gptChatService) {
        this.gptChatService = gptChatService;
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
}
