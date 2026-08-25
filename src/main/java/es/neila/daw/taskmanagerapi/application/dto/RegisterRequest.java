package es.neila.daw.taskmanagerapi.application.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) {
}
