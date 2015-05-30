package net.jonmiranda.prompts.storage;

import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.models.UserResponse;

import java.util.Date;
import java.util.List;

public interface Storage {

    UserResponse getResponse(Date date, Prompt prompt);

    UserResponse save(UserResponse userResponse, String response);

    Prompt save(Prompt prompt, boolean visible);

    Prompt getPrompt(String key);

    List<UserResponse> getAllResponses();

    void updatePrompt(String originalTitle, String newTitle);

    Prompt createPrompt(String title, boolean visible);

    List<Prompt> getPrompts();

    List<Prompt> getAllPrompts();

    void initializePrompts(String[] prompts);
}
