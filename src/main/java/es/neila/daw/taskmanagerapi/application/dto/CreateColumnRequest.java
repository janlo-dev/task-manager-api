package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record CreateColumnRequest(
        String name,
        int columnOrder,
        UUID boardId
) {
}
