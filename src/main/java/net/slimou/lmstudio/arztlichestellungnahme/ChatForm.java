package net.slimou.lmstudio.arztlichestellungnahme;

import java.util.Collections;
import java.util.List;

public class ChatForm {
    private String prompt;

    public List<String> getPrompt() {
        return Collections.singletonList(prompt);
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
