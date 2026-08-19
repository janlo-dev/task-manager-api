package es.neila.daw.taskmanagerapi.infrastructure.repository.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name ="Tasks")
@Getter
@Setter
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

    public TaskEntity(UUID id, String title, String description, UUID columnId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.columnId = columnId;
    }
}
