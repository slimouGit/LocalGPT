package net.slimou.lmstudio.transcriptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class TranscriptorController {

    private TranscriptorService transcriptorService;
    @Autowired
    public TranscriptorController(TranscriptorService transcriptorService) {
        this.transcriptorService = transcriptorService;
    }

    @GetMapping("/transcriptor")
    public String showTranscriptorPage(Model model) {
        return "transcriptor";
    }

    @PostMapping("/transcriptor/upload")
    public String handleFileUpload(@RequestParam("videoFile") MultipartFile file, RedirectAttributes redirectAttributes, Model model) {
        try {
            String filePath = transcriptorService.saveFile(file);
            String transcript = transcriptorService.generateTranscript(filePath);
            redirectAttributes.addFlashAttribute("message", "File uploaded successfully: " + file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("transcript", transcript);
            model.addAttribute("transcript", transcript);
            return "redirect:/transcriptor";
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload file: " + e.getMessage();
        }
    }
}