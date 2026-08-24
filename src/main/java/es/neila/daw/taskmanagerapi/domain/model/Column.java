package es.neila.daw.taskmanagerapi.domain.model;

import java.util.UUID;

public class Column {
    private  final UUID id;
    private  final UUID boardId; // realción con booard solo por ID
    private String name;
    private int columnOrder; // posición dentro del tablero

    public Column(UUID boardId, UUID id, String name, int columnOrder) {

        if(name == null || name.isBlank()){
            throw  new IllegalArgumentException("Column name cannot be empty");
        }

        this.boardId = boardId;
        this.id = id;
        this.name = name;
        this.columnOrder = columnOrder;
    }

    public void rename(String newName){
        if(newName == null || newName.isBlank()){
            throw new IllegalArgumentException("Column name cannot be empty");
        }
        this.name = newName;
    }

    public void changeOrder(int newOrder){
        if(newOrder < 0){
            throw new IllegalArgumentException("Column boardOrder cannot be negative");
        }
        this.columnOrder = newOrder;
    }

    public UUID getId() {return id;}
    public UUID getBoardId() {return boardId;}
    public String getName() {return name;}
    public int getColumnOrder() {return columnOrder;}
    public void setName(String name) {this.name = name;}
    public void setColumnOrder(int columnOrder) {this.columnOrder = columnOrder;}
}
