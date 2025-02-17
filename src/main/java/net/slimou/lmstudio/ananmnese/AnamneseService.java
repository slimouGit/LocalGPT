package net.slimou.lmstudio.ananmnese;

import org.springframework.stereotype.Service;

import java.util.regex.*;

@Service
public class AnamneseService {
    public static String extractRelevantSection(String pdfText, String keyword) {
        // Behebt den Fehler durch korrekt geschlossene Gruppe
        String regex = "(?i)" + Pattern.quote(keyword) + ".*?(?=(\\n\\d+\\.|\\n[A-Z]|$))";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(pdfText);
        StringBuilder results = new StringBuilder();

        while (matcher.find()) {
            results.append(matcher.group().trim()).append("\n\n");
        }

        return results.length() > 0 ? results.toString() : "Keine relevanten Informationen gefunden.";
    }
}