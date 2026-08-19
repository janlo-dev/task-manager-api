package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "boards")
public class BoardEntity {

    @Id
    private UUID id;

    @Column(name = "user_id")
    private UUID userdId;

    private String name;
    private int order;

    public BoardEntity() {
    }

    public BoardEntity(UUID id, UUID userdId, String name, int order) {
        this.id = id;
        this.userdId = userdId;
        this.name = name;
        this.order = order;
    }
}
