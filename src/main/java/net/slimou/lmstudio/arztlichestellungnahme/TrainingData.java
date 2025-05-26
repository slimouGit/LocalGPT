package net.slimou.lmstudio.arztlichestellungnahme;

import java.util.HashMap;
import java.util.Map;

public class TrainingData {

    private static final Map<String, String> trainingDataMap = new HashMap<>();

    static {
        trainingDataMap.put("F32.0", "Der Patient berichtet über anhaltende Stimmungstiefs, Antriebslosigkeit und Schlafstörungen. Die Symptome entsprechen einer leichten depressiven Episode (F32.0) und sind behandlungsbedürftig, jedoch ohne schwerwiegende Einschränkungen in den alltäglichen Aktivitäten.");
        trainingDataMap.put("J45.9", "Die Betroffene zeigt Symptome von episodischer Atemnot, insbesondere nachts und bei Belastung. Spirometrische Untersuchungen unterstützen die Diagnose von Asthma bronchiale, nicht näher bezeichnet (J45.9). Eine medikamentöse Therapie mit inhalativen Kortikosteroiden wird empfohlen.");
        trainingDataMap.put("E11.9", "Der Patient leidet an einem gut eingestellten Typ-2-Diabetes mellitus (E11.9) und essentieller Hypertonie (I10). Beide Diagnosen erfordern eine fortlaufende medikamentöse Therapie sowie eine strikte Lebensstilmodifikation, um langfristige Komplikationen zu vermeiden.");
        trainingDataMap.put("I10", "Der Patient leidet an einem gut eingestellten Typ-2-Diabetes mellitus (E11.9) und essentieller Hypertonie (I10). Beide Diagnosen erfordern eine fortlaufende medikamentöse Therapie sowie eine strikte Lebensstilmodifikation, um langfristige Komplikationen zu vermeiden.");
        trainingDataMap.put("M54.5", "Der Patient klagt über chronische Rückenschmerzen im unteren Lendenbereich, ohne neurologische Ausfallerscheinungen. Die Untersuchung bestätigt die Diagnose von Kreuzschmerzen (M54.5), die durch Physiotherapie und Schmerzmanagement behandelt werden sollten.");
        trainingDataMap.put("N39.0", "Die Patientin zeigte wiederholt Symptome einer unkomplizierten Harnwegsinfektion (N39.0) und einer akuten Gastroenteritis (K52.9). Beide Erkrankungen konnten konservativ therapiert werden, zeigen jedoch eine Tendenz zur Wiederkehr und erfordern präventive Maßnahmen.");
        trainingDataMap.put("K52.9", "Die Patientin zeigte wiederholt Symptome einer unkomplizierten Harnwegsinfektion (N39.0) und einer akuten Gastroenteritis (K52.9). Beide Erkrankungen konnten konservativ therapiert werden, zeigen jedoch eine Tendenz zur Wiederkehr und erfordern präventive Maßnahmen.");
        trainingDataMap.put("L20.9", "Die Patientin weist wiederkehrende Hautläsionen mit starkem Juckreiz auf, was mit einer atopischen Dermatitis (L20.9) vereinbar ist. Auslöser wie Allergene oder Stress sollten identifiziert und vermieden werden, während eine Basispflege und ggf. Kortikosteroide notwendig sind.");
        trainingDataMap.put("G40.9", "Der Patient hatte zwei generalisierte Krampfanfälle innerhalb von sechs Monaten. Die klinische und EEG-Untersuchung sprechen für eine Epilepsie, nicht näher bezeichnet (G40.9). Eine antiepileptische Medikation wurde eingeleitet und zeigt bisher gute Wirksamkeit.");
        trainingDataMap.put("C34.9", "Der Patient wurde mit einem bösartigen Tumor der Lunge (C34.9) diagnostiziert. Eine weiterführende Diagnostik und stadiengerechte Therapieplanung sind dringend erforderlich, da die Erkrankung bereits Symptome wie anhaltenden Husten und Gewichtsverlust verursacht hat.");
        trainingDataMap.put("F32.0_M54.5", "Die Patientin beschreibt Kreuzschmerzen (M54.5), die durch ihre depressive Stimmungslage (F32.0) verstärkt werden. Eine kombinierte Therapie aus psychotherapeutischen Ansätzen und physikalischer Therapie wird als geeignet angesehen, um beide Beschwerden zu lindern.");
        trainingDataMap.put("E11.9_N39.0", "Die Patientin mit langjährigem Typ-2-Diabetes mellitus (E11.9) klagt über wiederholte Harnwegsinfektionen (N39.0). Dies könnte auf eine diabetische Stoffwechsellage hinweisen, die optimiert werden sollte, um das Risiko weiterer Infektionen zu reduzieren.");
        trainingDataMap.put("M54.6", "Dem Patienten wird zur Unterstützung seiner Genesung dringend empfohlen, sich ausreichend Ruhe zu gönnen und möglichst viel zu schlafen. Eine Schlafdauer von mindestens acht bis zehn Stunden pro Tag ist anzustreben, um dem Körper die nötige Regeneration zu ermöglichen. Darüber hinaus ist auf eine hohe Flüssigkeitszufuhr zu achten: Der Patient sollte täglich mindestens 2,5 bis 3 Liter trinken, idealerweise in Form von Wasser, ungesüßtem Tee oder stark verdünnten Fruchtsäften. Hinsichtlich der Ernährung wird die Einnahme von Schonkost empfohlen. Diese sollte leicht verdaulich und magenfreundlich sein, beispielsweise bestehend aus gekochtem Gemüse, Reis, Zwieback, mild gewürzten Suppen sowie magerem Fleisch oder Fisch. Auf den Konsum von Alkohol ist vollständig zu verzichten, da dieser den Heilungsprozess erheblich beeinträchtigen und den Organismus zusätzlich belasten kann. Die genannten Maßnahmen sind konsequent umzusetzen, um eine rasche und nachhaltige Besserung des Gesundheitszustandes zu fördern.");


    }

    public static String getExampleText(String icdCode) {
        return trainingDataMap.getOrDefault(icdCode, "Kein Beispieltext verfügbar.");
    }
}