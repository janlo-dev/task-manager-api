package es.neila.daw.taskmanagerapi.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserTaskProjectionResponse(
        UUID taskId,
        String taskTitle,
        String taskDescription,
        UUID columnId,
        String columnName,
        UUID boardId,
        String boardName,
        LocalDateTime createdAt
) {
}
