package net.slimou.lmstudio.ananmnese;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AnamneseController {

    @GetMapping("/anamnese")
    public String anamnese(Model model) {
        return "anamnese";
    }
}