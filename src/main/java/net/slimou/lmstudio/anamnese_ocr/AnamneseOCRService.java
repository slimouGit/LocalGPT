package net.slimou.lmstudio.anamnese_ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnamneseOCRService {
    private final ITesseract tesseract;

    public AnamneseOCRService() {
        tesseract = new Tesseract();
        tesseract.setDatapath("C:/Program Files/Tesseract-OCR/tessdata"); // Anpassen je nach Betriebssystem
        tesseract.setLanguage("deu"); // Deutsche Sprache für OCR
    }

    /**
     * Durchsucht PDFs nach einem Begriff und gibt relevante Textstellen zurück.
     */
    public List<String> searchInPDFs(String directory, String keyword) {
        List<String> matchingTexts = new ArrayList<>();
        File dir = new File(directory);

        if (!dir.exists() || !dir.isDirectory()) return matchingTexts;

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".pdf")) {
                try {
                    String text = extractTextFromPDF(file);
                    List<String> results = extractRelevantSentences(text, keyword);

                    if (!results.isEmpty()) {
                        matchingTexts.add("🔍 **Ergebnisse in Datei:** " + file.getName());
                        matchingTexts.addAll(results);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return matchingTexts;
    }

    /**
     * Extrahiert Text aus einer PDF-Datei mit OCR-Fallback.
     */
    private String extractTextFromPDF(File file) throws IOException, TesseractException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        // Falls der Text leer ist, versuche OCR mit Tesseract
        if (text.trim().isEmpty()) {
            text = tesseract.doOCR(file);
        }
        return text;
    }

    /**
     * Extrahiert alle relevanten Sätze oder Absätze mit dem Suchbegriff.
     */
    private List<String> extractRelevantSentences(String text, String keyword) {
        List<String> results = new ArrayList<>();
        Pattern pattern = Pattern.compile("([^\\.\\n]*?\\b" + keyword + "\\b[^\\.\\n]*[\\.\\n])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            results.add(matcher.group(1).trim());
        }
        return results;
    }
}
