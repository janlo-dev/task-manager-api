package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.column;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name ="columns")
@Getter
@Setter
public class ColumnEntity {

    @Id
    private UUID id;

    @Column(name = "board_id")
    private UUID boardId;

    private String name;

    @Column(name = "column_order")
    private int columnOrder;

    public ColumnEntity() {}

    public ColumnEntity(UUID id, UUID boardId, String name, int columnOrder) {
        this.id = id;
        this.boardId = boardId;
        this.name = name;
        this.columnOrder = columnOrder;
    }
}
