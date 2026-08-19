package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name ="columns")
public class ColumnEntity {

    @Id
    private UUID id;

    @Column(name = "board_id")
    private UUID boardId;

    private String name;
    private int order;

    public ColumnEntity() {}

    public ColumnEntity(UUID id, UUID boardId, String name, int order) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.order = order;
    }
}
