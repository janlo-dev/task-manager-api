package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record CreateBoardRequest(
        String name,
        int boardOrder,
        UUID userId
) {
}
