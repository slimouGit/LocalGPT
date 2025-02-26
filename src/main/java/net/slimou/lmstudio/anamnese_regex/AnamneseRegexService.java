package net.slimou.lmstudio.anamnese_regex;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnamneseRegexService {
    public static String extractRelevantSection(String pdfText, String keyword) {
        // Adjust regex to handle line breaks and capture relevant sections
        String regex = "(?i)" + Pattern.quote(keyword) + "(?::\\s*|\\s*\\n\\s*)(.*?)(?=(\\n\\d+\\.|\\n[A-Z]|$))";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pdfText);
        StringBuilder results = new StringBuilder();

        while (matcher.find()) {
            results.append(matcher.group(1).trim()).append("\n\n");
        }

        return results.length() > 0 ? results.toString() : "Keine relevanten Informationen gefunden.";
    }
}