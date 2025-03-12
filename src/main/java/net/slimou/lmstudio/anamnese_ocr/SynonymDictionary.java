package net.slimou.lmstudio.anamnese_ocr;

import java.util.HashMap;
import java.util.Map;

public class SynonymDictionary {
    private static final Map<String, String[]> synonyms = new HashMap<>();

    static {
        synonyms.put("allergie", new String[]{"allergien", "allergic reaction", "hypersensitivity"});
        synonyms.put("operation", new String[]{"operationen", "operations", "operation"});
        // Add more synonyms as needed
    }

    public static String[] getSynonyms(String keyword) {
        return synonyms.getOrDefault(keyword.toLowerCase(), new String[]{keyword});
    }
}
