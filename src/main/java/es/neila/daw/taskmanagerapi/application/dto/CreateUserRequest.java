package es.neila.daw.taskmanagerapi.application.dto;

public record CreateUserRequest(
        String name,
        String email
) {
}
