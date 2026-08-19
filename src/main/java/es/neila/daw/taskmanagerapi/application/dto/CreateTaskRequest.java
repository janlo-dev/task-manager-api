package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record CreateTaskRequest(
    String title,
    String description,
    UUID columnId
    )
{}
