package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record UpdateTaskDescriptionRequest(
        UUID taskId,
        String newDescription
) {
}
