package es.neila.daw.taskmanagerapi.domain.event;

import java.util.UUID;

public record ColumnDeletedEvent(
        UUID columnId,
        UUID performedByUserId
) {
}
