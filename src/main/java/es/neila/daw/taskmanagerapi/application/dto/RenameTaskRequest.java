package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record RenameTaskRequest(
        UUID taskId,
        String newTitle
) {
}
