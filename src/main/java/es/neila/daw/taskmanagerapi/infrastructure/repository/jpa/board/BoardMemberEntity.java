package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "board_members")
@Getter
@Setter
public class BoardMemberEntity {

    @Id
    private UUID id;

    @Column(name = "board_id", nullable = false)
    private UUID boardId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BoardRole role;

    public BoardMemberEntity() {
    }

    public BoardMemberEntity(UUID id, UUID boardId, UUID userId, BoardRole role) {
        this.id = id;
        this.boardId = boardId;
        this.userId = userId;
        this.role = role;
    }
}
