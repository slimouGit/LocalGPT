package net.slimou.lmstudio.openai;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class OpenAiController {

    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @GetMapping("/openai")
    public String showOpenAiPage() {
        return "openai";
    }

    @PostMapping("/openai")
    public String handleUserInput(@RequestParam("userInput") String userInput, Model model) {
        String response = openAiService.getChatResponse(userInput);
        model.addAttribute("response", response);
        return "openai";
    }
}
