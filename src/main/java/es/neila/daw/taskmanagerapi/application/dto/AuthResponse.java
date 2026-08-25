package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String accessToken
) {
}
