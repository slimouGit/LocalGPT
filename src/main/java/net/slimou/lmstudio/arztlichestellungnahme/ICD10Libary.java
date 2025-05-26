package net.slimou.lmstudio.arztlichestellungnahme;

import java.util.HashMap;
import java.util.Map;

public class ICD10Libary {
    public static final Map<String, String> icd10Map = new HashMap<>();
    static {
        icd10Map.put("F32.0", "Leichte depressive Episode");
        icd10Map.put("J45.9", "Asthma, nicht näher bezeichnet");
        icd10Map.put("E11.9", "Typ-2-Diabetes mellitus ohne Komplikationen");
        icd10Map.put("I10", "Essentielle Hypertonie");
        icd10Map.put("M54.5", "Kreuzschmerz");
        icd10Map.put("N39.0", "Harnwegsinfektion, nicht näher bezeichnet");
        icd10Map.put("K52.9", "Nichtinfektiöse Gastroenteritis und Kolitis, nicht näher bezeichnet");
        icd10Map.put("L20.9", "Atopische Dermatitis");
        icd10Map.put("G40.9", "Epilepsie, nicht näher bezeichnet");
        icd10Map.put("C34.9", "Bösartige Neubildung der Bronchien oder der Lunge");
        icd10Map.put("F13.3", "Abhängigkeitssyndrom durch Sedativa oder Hypnotika");
        icd10Map.put("M54.6", "Schmerzen im unteren Rückenbereich");
    }

}
