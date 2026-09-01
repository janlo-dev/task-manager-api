package es.neila.daw.taskmanagerapi.application.dto;

import java.util.UUID;

public record InviteBoardMemberRequest(
        UUID boardId,
        String email
) {
}
