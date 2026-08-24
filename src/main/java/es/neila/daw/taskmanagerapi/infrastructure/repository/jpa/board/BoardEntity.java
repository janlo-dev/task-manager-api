package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "boards")
@Getter
@Setter
public class BoardEntity {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userdId;

    private String name;

    @Column(name = "board_order")
    private int boardOrder;

    public BoardEntity() {
    }

    public BoardEntity(UUID id, UUID userdId, String name, int boardOrder) {
        this.id = id;
        this.userdId = userdId;
        this.name = name;
        this.boardOrder = boardOrder;
    }
}
