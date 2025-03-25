package net.slimou.lmstudio.watsonx;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WatsonXController {

    private WatsonXService watsonXService;

    public WatsonXController(WatsonXService watsonXService) {
        this.watsonXService = watsonXService;
    }

    @GetMapping("/watsonx")
    public String showWatsonxPage() {
        return "watsonx";
    }

    @PostMapping("/watsonx")
    public String handleWatsonxInput(@RequestParam("userInput") String userInput, Model model) {
        String response = watsonXService.getWatsonxResponse(userInput);
        model.addAttribute("response", response);
        return "watsonx";
    }

}
