package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record RenameBoardRequest(
        UUID boardId,
        String newName
) {
}
