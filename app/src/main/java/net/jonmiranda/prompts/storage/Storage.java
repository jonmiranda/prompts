package net.jonmiranda.prompts.storage;

import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.models.UserResponse;

import java.util.Date;
import java.util.List;

public interface Storage {

    UserResponse getResponse(Date date, Prompt prompt);

    UserResponse save(UserResponse userResponse, String response);

    Prompt getPrompt(String title);

    List<UserResponse> getAllResponses();

    List<Prompt> getPrompts();

    void initializePrompts(String[] prompts);
}
