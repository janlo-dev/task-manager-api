package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record MoveTaskRequest(
        UUID taskId,
        UUID newColumnId
)
{}
