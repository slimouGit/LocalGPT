package net.slimou.lmstudio.ananmnese;

import org.springframework.stereotype.Service;

@Service
public class AnamneseService {
    private final LMStudioClient lmStudioClient;

    public AnamneseService(LMStudioClient lmStudioClient) {
        this.lmStudioClient = lmStudioClient;
    }

    public String analyzePdf(String filePath, String searchTerm) {
        try {
            String pdfText = PdfTextExtractor.extractTextFromPdf(filePath);
            return lmStudioClient.searchTextInPdf(pdfText, searchTerm);
        } catch (Exception e) {
            return "Fehler bei der Analyse: " + e.getMessage();
        }
    }
}