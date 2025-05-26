package net.slimou.lmstudio.arztlichestellungnahme;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.slimou.lmstudio.Config.SYSTEM_PROMPT;

public class PromptGenerator {

    public static String generateSystemPrompt(List<String> selectedIcds) {
        String exampleTexts = getExamples(selectedIcds);

        String systemPrompt = "Beschreibe die gesundheitliche Situation des Patienten, erläutere die empfohlene Behandlung und gib eine Einschätzung zur Prognose basierend auf dem oder den angegebenen ICD-Codes. " +
                "Orientiere dich dabei bevorzugt an den Handlungsempfehlungen aus den folgenden Beispielen: " + exampleTexts +
                ". Priorisiere diese Beispiele auch dann, wenn die Diagnose aus den Beispielen von der aktuellen ICD-Codierung abweicht." +
                "Die Stellungnahme soll als zusammenhängender Fließtext ohne Einleitung, ohne separate Überschriften und ohne Stichpunkte formuliert sein. Es sollen keine Phrasen wie 'Hier ist eine Stellungnahme' oder 'Der Patient wurde untersucht' verwendet werden. Stattdessen soll der Text direkt mit der Schilderung der gesundheitlichen Situation des Patienten beginnen. Die Informationen zu den Diagnosen, möglichen Behandlungsmaßnahmen und der Prognose müssen logisch ineinander übergehen und präzise sowie professionell formuliert sein.";

        return systemPrompt;
    }

    private static String getExamples(List<String> selectedIcds) {
        StringBuilder examples = new StringBuilder();
        List<String> icds = extractICDCodes(selectedIcds.toString());
        String icdCode = formatICDCodes(icds.toString());
        for(String selectedIcd : selectedIcds){
            examples.append(TrainingData.getExampleText(ermittelIcdCode(selectedIcd)));
        }
        String exampleTexts = selectedIcds.stream()
                .map(icd -> TrainingData.getExampleText(ermittelIcdCode(icd)))
                .collect(Collectors.joining(" ------------------------------------------ "));
        return exampleTexts;
    }

    public static String generateUserPrompt(List<String> selectedIcds) {
        String userPrompt = "Der Patient wurde mit dem ICD-Code " + selectedIcds + " diagnostiziert. " +
                "Bitte beschreibe die gesundheitliche Situation, die empfohlene Behandlung und gib eine Einschätzung zur Prognose.";

        return userPrompt;
    }

    private static String ermittelIcdCode(String icd) {
        if (icd.contains(":")) {
            return icd.split(":")[0].trim();
        }
        return icd.trim();
    }

    public static List<String> extractICDCodes(String selectedIcds) {
        return Arrays.stream(selectedIcds.split(","))
                .map(entry -> entry.split(":")[0].trim())
                .collect(Collectors.toList());
    }

    public static String formatICDCodes(String selectedIcds) {
        return Arrays.stream(selectedIcds.split(","))
                .map(entry -> entry.split(":")[0].trim())
                .collect(Collectors.joining(" , "));
    }
}