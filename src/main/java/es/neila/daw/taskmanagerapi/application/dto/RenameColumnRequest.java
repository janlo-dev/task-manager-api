package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record RenameColumnRequest(
        UUID columnId,
        String newName
) {}
