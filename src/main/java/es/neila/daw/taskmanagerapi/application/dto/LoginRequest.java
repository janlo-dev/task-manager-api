package es.neila.daw.taskmanagerapi.application.dto;

public record LoginRequest(
        String email,
        String password
) {
}
