package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name ="Tasks")
public class TaskEntity {

    @Id
    private UUID id;

    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt ;

    @Column(name = "column_id")
    private UUID columnId;

    public TaskEntity() {}

    public TaskEntity(UUID id, String tittle, String description, UUID columnId) {
        this.id = id;
        this.title = tittle;
        this.description = description;
        this.columnId = columnId;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
