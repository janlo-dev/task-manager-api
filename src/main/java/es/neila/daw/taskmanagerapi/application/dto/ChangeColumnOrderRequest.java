package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record ChangeColumnOrderRequest(
        UUID columnId,
        int newOrder
) {
}
