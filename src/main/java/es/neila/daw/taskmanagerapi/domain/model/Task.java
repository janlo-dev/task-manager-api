package es.neila.daw.taskmanagerapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private final UUID id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt ;
    private UUID columnId; // realción con columna( solo por ID, no por objeto)

    public Task(UUID id, String title, String description, UUID columnId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt  = LocalDateTime.now();
        this.columnId = columnId;
    }

    public Task(UUID id, String title, String description,UUID columnId,
                LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.columnId = columnId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public void rename(String newTitle){
        if(newTitle == null || newTitle.isBlank()){
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        this.title = newTitle;
        this.updatedAt  = LocalDateTime.now();
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }

    public void moveToColumn(UUID newColumnId) {
        this.columnId = newColumnId;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {return id;}
    public String getTitle() {return title;}
    public String getDescription() {return description;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public UUID getColumnId() {return columnId;}
}
