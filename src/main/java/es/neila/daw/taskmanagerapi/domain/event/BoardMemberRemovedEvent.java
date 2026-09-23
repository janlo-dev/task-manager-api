package es.neila.daw.taskmanagerapi.domain.event;

import java.util.UUID;

public record BoardMemberRemovedEvent(
        UUID boardId,
        UUID userId,
        UUID performedByUserId
) {
}
