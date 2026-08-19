package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record CreateBoardRequest(
        String name,
        int order,
        UUID userId
) {
}
