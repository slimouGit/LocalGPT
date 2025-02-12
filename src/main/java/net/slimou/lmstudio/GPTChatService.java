package net.slimou.lmstudio;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@Service
public class GPTChatService {

    private static final String LOCAL_API_URL = "http://localhost:1234/v1/chat/completions";
    private final RestTemplate restTemplate;

    public GPTChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCompletion(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = "{\"model\": \"granite-3.1-1b-a400m-instruct\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(LOCAL_API_URL, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }
}
