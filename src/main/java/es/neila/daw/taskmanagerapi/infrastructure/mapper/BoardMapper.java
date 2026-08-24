package es.neila.daw.taskmanagerapi.infrastructure.mapper;

import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.infrastructure.repository.jpa.board.BoardEntity;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

    public BoardEntity toEntity(Board board) {
        return new BoardEntity(
                board.getId(),
                board.getUserId(),
                board.getName(),
                board.getBoardOrder()
        );
    }

    public Board toDomain(BoardEntity entity) {
        return new Board(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getBoardOrder()
        );
    }
}
