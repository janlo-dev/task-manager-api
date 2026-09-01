package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record AssignTaskRequest(
        UUID taskId,
        UUID assignedUserId
) {
}
