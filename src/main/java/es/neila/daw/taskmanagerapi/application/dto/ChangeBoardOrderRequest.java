package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record ChangeBoardOrderRequest(
        UUID boardId,
        int newOrder
) {
}
