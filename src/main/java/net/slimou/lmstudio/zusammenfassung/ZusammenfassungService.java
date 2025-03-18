package net.slimou.lmstudio.zusammenfassung;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final WebClient webClient;

    public ZusammenfassungService(RestTemplate restTemplate, ObjectMapper objectMapper, WebClient.Builder webClientBuilder) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com").build();
    }

    public String getZusammenfassung() {
        ResponseEntity<String> response = null;
        try {
            String anamneseGespraech = getAnamneseGespraech();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

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

    private static String getAnamneseGespraech() throws IOException {
        Path filePath = Paths.get("src/main/resources/AnamneseGespraech.txt");
        String anamneseGespraech = Files.readString(filePath);
        return anamneseGespraech;
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

    public String getDataForKeyword(String keyword, String source) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String anamneseGespraech;
        try {
            anamneseGespraech = getAnamneseGespraech();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String prompt = "Finde zu dem folgendem Keyword '" + keyword + "' Informationen in dem folgenden Gespräch zwischen einem Arzt und einem Patienten '" + anamneseGespraech + "' und Liste nur die bezogen auf das Keyword '"+keyword+"'relevanten Informationen in einem einzigen Satz mit maximal 100 Zeichen auf.";

        List<Map<String, String>> messages = List.of(
                Map.of("role", "user", "content", prompt)
        );
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", MAX_TOKENS);
        requestBody.put("temperature", TEMPERATURE);


        String result = "";
        if (source.equals("local")) {
            result = getLocalResponse(requestBody, headers);
        }else {
            result =  geRemoteResponse(requestBody);
        }
        return result;
    }

    private String geRemoteResponse(Map<String, Object> requestBody){
        System.out.print("REMOTE");
        requestBody.put("model", "gpt-4o");
        String response = webClient.post()
                .uri("/v1/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + API_KEY)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.at("/choices/0/message/content").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error parsing response";
        }
    }

    private String getLocalResponse(Map<String, Object> requestBody, HttpHeaders headers) {
        System.out.print("LOKAL");
        requestBody.put("model", MODEL_NAME);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(LOCAL_API_URL, HttpMethod.POST, entity, String.class);
        return extractContentFromResponse(response.getBody());
    }
}
