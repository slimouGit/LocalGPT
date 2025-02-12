package net.slimou.lmstudio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class GPTChatController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("message", "Willkommen auf der Startseite!");
        return "index";
    }
}
