package es.neila.daw.taskmanagerapi.domain.event;

import java.util.UUID;

public record BoardDeletedEvent(
        UUID boardId,
        UUID performedByUserId
) {
}
