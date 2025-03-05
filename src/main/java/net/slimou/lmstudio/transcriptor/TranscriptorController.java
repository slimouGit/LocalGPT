package net.slimou.lmstudio.transcriptor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TranscriptorController {

    private final TranscriptorService transcriptorService;

    @Autowired
    public TranscriptorController(TranscriptorService transcriptorService) {
        this.transcriptorService = transcriptorService;
    }

    @GetMapping("/transcriptor")
    public String showTranscriptorPage(Model model) {
        return "transcriptor";
    }
}
