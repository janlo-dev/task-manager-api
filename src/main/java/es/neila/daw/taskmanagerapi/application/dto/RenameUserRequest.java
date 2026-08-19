package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record RenameUserRequest(
        UUID userId,
        String newName
) {
}
