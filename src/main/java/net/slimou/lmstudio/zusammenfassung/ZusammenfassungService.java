package net.slimou.lmstudio.zusammenfassung;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.slimou.lmstudio.arztlichestellungnahme.PromptGenerator;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.slimou.lmstudio.Config.*;

@Service
public class ZusammenfassungService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public ZusammenfassungService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String getZusammenfassung() {
        ResponseEntity<String> response = null;
        try {
            // Lade den Inhalt der Datei
            Path filePath = Paths.get("src/main/resources/AnamneseGespraech.txt");
            String anamneseGespraech = Files.readString(filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Erstelle den Prompt
            String prompt = "Bitte analysiere das folgende Arztgespräch und erstelle eine strukturierte Stichpunkt-Zusammenfassung mit den folgenden Kategorien:\n\n" +
                    "- Gesundheitliches Problem\n" +
                    "- Falls Schmerzen, welche Art von Schmerzen und in welchem Ausmaß\n" +
                    "- Medikation\n" +
                    "- Was aktuell unternommen wird\n" +
                    "- Was empfohlen wird, zu unternehmen\n\n" +
                    "Das Gespräch:\n\n" + anamneseGespraech;

            // Erstelle den JSON-Body mit einer Map
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("model", MODEL_NAME);

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
            messages.add(Map.of("role", "user", "content", prompt));

            requestBodyMap.put("messages", messages);
            requestBodyMap.put("max_tokens", MAX_TOKENS);
            requestBodyMap.put("temperature", TEMPERATURE);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            response = restTemplate.exchange(LOCAL_API_URL, HttpMethod.POST, entity, String.class);

        } catch (IOException e) {
            e.printStackTrace();
            return "Fehler beim Laden der Datei AnamneseGespraech.txt";
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler beim Verarbeiten des Requests";
        }

        return extractContentFromResponse(response.getBody());
    }


    private String extractContentFromResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }
}
