package es.neila.daw.taskmanagerapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {

    private final UUID id;
    private String tittle;
    private String description;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt ;
    private UUID columnId; // realción con columna( solo por ID, no por objeto)

    public Task(UUID id, String tittle, String description, UUID columnId) {
        this.id = id;
        this.tittle = tittle;
        this.description = description;
        this.createAt = LocalDateTime.now();
        this.updatedAt  = LocalDateTime.now();
        this.columnId = columnId;
    }


    public void rename(String newTitle){
        if(newTitle == null || newTitle.isBlank()){
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        this.tittle = newTitle;
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
    public String getTittle() {return tittle;}
    public String getDescription() {return description;}
    public LocalDateTime getCreateAt() {return createAt;}
    public LocalDateTime getUpdatedAt() {return updatedAt;}
    public UUID getColumnId() {return columnId;}
}
