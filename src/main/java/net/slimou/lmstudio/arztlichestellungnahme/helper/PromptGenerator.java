package net.slimou.lmstudio.arztlichestellungnahme.helper;

public class PromptGenerator {

    public static String generateSystemPrompt(String exampleTexts) {

        String systemPrompt = "Beschreibe die gesundheitliche Situation des Patienten, erläutere die empfohlene Behandlung und gib eine Einschätzung zur Prognose basierend auf dem oder den angegebenen ICD-Codes. " +
                "Orientiere dich dabei bevorzugt an den Handlungsempfehlungen aus den folgenden Beispielen: " + exampleTexts +
                ". Priorisiere diese Beispiele auch dann, wenn die Diagnose aus den Beispielen von der aktuellen ICD-Codierung abweicht." +
                "Die Stellungnahme soll als zusammenhängender Fließtext ohne Einleitung, ohne separate Überschriften und ohne Stichpunkte formuliert sein. Es sollen keine Phrasen wie 'Hier ist eine Stellungnahme' oder 'Der Patient wurde untersucht' verwendet werden. Stattdessen soll der Text direkt mit der Schilderung der gesundheitlichen Situation des Patienten beginnen. Die Informationen zu den Diagnosen, möglichen Behandlungsmaßnahmen und der Prognose müssen logisch ineinander übergehen und präzise sowie professionell formuliert sein.";
        return systemPrompt;
    }

    public static String generateUserPrompt(String diagnose) {
        String userPrompt = "Der Patient wurde mit folgender Diagnose " + diagnose + " untersucht. " +
                "Beschreibe die gesundheitliche Situation, die empfohlene Behandlung und gib eine Einschätzung zur Prognose.";
        return userPrompt;
    }



}