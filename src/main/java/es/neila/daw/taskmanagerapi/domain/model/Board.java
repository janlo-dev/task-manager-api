package es.neila.daw.taskmanagerapi.domain.model;

import java.util.UUID;

public class Board {

    private  final UUID id;
    private  final UUID userdId; //realción con User solo por ID
    private String name;
    private int boardOrder; // posición del tablero dentro del usuario

    public Board(UUID id, UUID userdId, String name, int boardOrder) {

        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Board name cannot be empty");
        }
        if(boardOrder < 0){
            throw new IllegalArgumentException("Board order cannot be negative");
        }

        this.id = id;
        this.userdId = userdId;
        this.name = name;
        this.boardOrder = boardOrder;
    }

    public void rename(String newName){
        if(newName == null || newName.isBlank()){
            throw new IllegalArgumentException("Board name cannot be empty");
        }
        this.name = newName;
    }

    public void changeOrder(int newOrder){
        if(newOrder < 0){
            throw new IllegalArgumentException("Board order cannot be negative");
        }
        this.boardOrder = newOrder;
    }

    public UUID getId() {return id;}

    public UUID getUserId() {return userdId;}

    public String getName() {return name;}

    public int getBoardOrder() {return boardOrder;}
}
