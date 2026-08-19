package es.neila.daw.taskmanagerapi.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Column {
    private  final UUID id;
    private  final UUID boardId; // realción con booard solo por ID
    private String name;
    private int order; // posición dentro del tablero

    public Column(UUID boardId, UUID id, String name, int order) {

        if(name == null || name.isBlank()){
            throw  new IllegalArgumentException("Column name cannot be empty");
        }

        this.boardId = boardId;
        this.id = id;
        this.name = name;
        this.order = order;
    }

    public void rename(String newName){
        if(newName == null || newName.isBlank()){
            throw new IllegalArgumentException("Column name cannot be empty");
        }
        this.name = newName;
    }

    public void changeOrder(int newOrder){
        if(newOrder < 0){
            throw new IllegalArgumentException("Column order cannot be negative");
        }
        this.order = newOrder;
    }

    public UUID getId() {return id;}
    public UUID getBoardId() {return boardId;}
    public void setName(String name) {this.name = name;}
    public void setOrder(int order) {this.order = order;}
}
