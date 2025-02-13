package net.slimou.lmstudio;

import java.util.List;
import java.util.stream.Collectors;

import static net.slimou.lmstudio.Config.SYSTEM_PROMPT;

public class PromptGenerator {

    public static String generatePrompt(List<String> selectedIcds, String enteredDiagnosis) {
        String systemPrompt = SYSTEM_PROMPT;

        String exampleTexts = selectedIcds.stream()
                .map(icd -> TrainingData.getExampleText(icd))
                .collect(Collectors.joining(" "));

        String combinedPrompt = "Diagnose: " + enteredDiagnosis + ". " + selectedIcds;

        String additionalInstructions = "Beschreibe die gesundheitliche Situation des Patienten, erläutere die empfohlene Behandlung und gib eine Einschätzung zur Prognose basierend auf den oder die angegebenen ICD-Code.";

        return systemPrompt + combinedPrompt + additionalInstructions;
    }
}