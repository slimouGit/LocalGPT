package net.slimou.lmstudio.zusammenfassung;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    public String getWatsonXZusammenfassung() {
        try {
            String anamneseGespraech = getAnamneseGespraech();
            String accessToken = fetchWatsonXAccessToken(ICA_API_KEY);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            System.out.println("🔐 Access Token: " + accessToken);

//            String prompt = "Bitte analysiere das folgende Arztgespräch und erstelle eine strukturierte Stichpunkt-Zusammenfassung mit den folgenden Kategorien:\n\n" +
//                    "- Gesundheitliches Problem\n" +
//                    "- Falls Schmerzen, welche Art von Schmerzen und in welchem Ausmaß\n" +
//                    "- Medikation\n" +
//                    "- Was aktuell unternommen wird\n" +
//                    "- Was empfohlen wird, zu unternehmen\n\n" +
//                    "Das Gespräch:\n\n" + anamneseGespraech;

            String prompt = "Tell a joke";

            // WatsonX benötigt meist ein Format wie:
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("model", "Granite 3.1 8B");
            requestBodyMap.put("input", prompt);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("decoding_method", "greedy");
            parameters.put("max_new_tokens", 500);
            parameters.put("temperature", 0.3);

            requestBodyMap.put("parameters", parameters);

            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(requestBodyMap);

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://us-south.ml.cloud.ibm.com/ml/v1/text/generation?version=2024-03-21",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return extractWatsonXContentFromResponse(response.getBody());

        } catch (IOException e) {
            e.printStackTrace();
            return "Fehler beim Laden der Datei AnamneseGespraech.txt";
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler beim Verarbeiten des WatsonX Requests";
        }
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

    private String extractWatsonXContentFromResponse(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("results").get(0).path("generated_text").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "Fehler beim Parsen der WatsonX-Antwort";
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
        String prompt = "Finde zu dem folgendem Keyword '" + keyword + "' Informationen in dem folgenden Gespräch zwischen einem Arzt und einem Patienten '" + anamneseGespraech + "' und Liste nur die bezogen auf das Keyword '"+keyword+"'relevanten Informationen in einem einzigen Satz mit maximal 100 Zeichen auf. Verzichte auf redundante Angaben und erwähne niemals personenbezogenen Daten wie Vor- und Zunamen oder das Alter des Patienten.";

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

    private String fetchWatsonXAccessToken(String apiKey) {
        RestTemplate tokenTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "urn:ibm:params:oauth:grant-type:apikey");
        body.add("apikey", apiKey);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = tokenTemplate.exchange(
                    "https://iam.cloud.ibm.com/identity/token",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Abrufen des WatsonX Access Tokens");
        }
    }

}
