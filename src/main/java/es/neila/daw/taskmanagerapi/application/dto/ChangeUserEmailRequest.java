package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record ChangeUserEmailRequest(
        UUID userId,
        String newEmail
) {
}
