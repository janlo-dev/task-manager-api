package es.neila.daw.taskmanagerapi.application.dto;

import es.neila.daw.taskmanagerapi.domain.model.BoardRole;

import java.util.UUID;

public record BoardMemberDetailsResponse(
        UUID memberId,
        UUID userId,
        String email,
        String name,
        BoardRole role
) {
}
