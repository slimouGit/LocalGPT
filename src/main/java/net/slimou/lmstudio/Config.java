package net.slimou.lmstudio;

public class Config {
    public static final String LOCAL_API_URL = "http://localhost:1234/v1/chat/completions";
//    public static final String MODEL_NAME = "granite-3.1-1b-a400m-instruct";
//    public static final String MODEL_NAME = "qwen2.5-14b-instruct";
    public static final String MODEL_NAME = "llama-3.2-1b-creative-rp";
//    public static final String MODEL_NAME = "meta-llama-3.1-8b-instruct";
    public static final String SYSTEM_PROMPT = "Schreibe eine professionelle ärztliche Stellungnahme basierend auf den folgenden ICD-10-Codes für einen Patienten, der Kunde des Arbeitsamtes ist und vom Ärztlichen Dienst des Arbeitsamtes untersucht wurde. Die Stellungnahme soll als zusammenhängender Fließtext ohne Einleitung, ohne separate Überschriften und ohne Stichpunkte formuliert sein. Es sollen keine Phrasen wie 'Hier ist eine Stellungnahme' oder 'Der Patient wurde untersucht' verwendet werden. Stattdessen soll der Text direkt mit der Schilderung der gesundheitlichen Situation des Patienten beginnen. Die Informationen zu den Diagnosen, möglichen Behandlungsmaßnahmen und der Prognose müssen logisch ineinander übergehen und präzise sowie professionell formuliert sein. Die Texte sollen vollständig sein.";
    public static final int MAX_TOKENS = 500;
    public static final double TEMPERATURE = 0.7;
}
