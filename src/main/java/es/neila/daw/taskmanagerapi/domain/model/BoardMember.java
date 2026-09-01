package es.neila.daw.taskmanagerapi.domain.model;

import java.util.UUID;

public class BoardMember {

    private final UUID id;
    private final UUID boardId;
    private final UUID userId;
    private final BoardRole role;

    public BoardMember(UUID id, UUID boardId, UUID userId, BoardRole role) {
        if (boardId == null || userId == null || role == null) {
            throw new IllegalArgumentException("BoardMember requires boardId, userId and role");
        }
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.role = role;
    }

    public UUID getId() { return id; }
    public UUID getBoardId() { return boardId; }
    public UUID getUserId() { return userId; }
    public BoardRole getRole() { return role; }
}
