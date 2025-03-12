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
                        matchingTexts.add("🔍 **Results in file:** " + file.getName());
                        matchingTexts.addAll(results);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return matchingTexts;
    }

    private String extractTextFromPDF(File file) throws IOException, TesseractException {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();

        if (text.trim().isEmpty()) {
            text = tesseract.doOCR(file);
        }
        return text;
    }

    private List<String> extractRelevantSentences(String text, String keyword) {
        List<String> results = new ArrayList<>();
        String[] keywords = SynonymDictionary.getSynonyms(keyword);
        StringBuilder patternBuilder = new StringBuilder();

        for (String key : keywords) {
            if (patternBuilder.length() > 0) {
                patternBuilder.append("|");
            }
            patternBuilder.append("\\b").append(Pattern.quote(key)).append("\\b");
        }

        Pattern pattern = Pattern.compile("([^\\.\\n]*?(" + patternBuilder.toString() + ")[^\\.\\n]*[\\.\\n])", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String sentence = matcher.group(1).trim();
            String additionalInfo = extractAdditionalInfo(sentence, keyword);
            results.add(sentence + " " + additionalInfo);
        }
        return results;
    }

    private String extractAdditionalInfo(String sentence, String keyword) {
        StringBuilder additionalInfo = new StringBuilder();

        // Extract dates
        Pattern datePattern = Pattern.compile("\\b\\d{1,2}\\.\\d{1,2}\\.\\d{2,4}\\b");
        Matcher dateMatcher = datePattern.matcher(sentence);
        while (dateMatcher.find()) {
            additionalInfo.append(" Date: ").append(dateMatcher.group());
        }

        // Extract operation types (example pattern, adjust as needed)
        Pattern operationPattern = Pattern.compile("\\b(" + keyword + "|operation|surgery|procedure)\\b", Pattern.CASE_INSENSITIVE);
        Matcher operationMatcher = operationPattern.matcher(sentence);
        while (operationMatcher.find()) {
            additionalInfo.append(" Type: ").append(operationMatcher.group());
        }

        return additionalInfo.toString();
    }
}