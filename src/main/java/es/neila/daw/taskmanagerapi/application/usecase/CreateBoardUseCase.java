package es.neila.daw.taskmanagerapi.application.usecase;

import es.neila.daw.taskmanagerapi.application.dto.CreateBoardRequest;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.UUID;

public class CreateBoardUseCase {

    private final BoardRepository boardRepository;

    public CreateBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board execute(CreateBoardRequest request) {
        Board board = new Board(
                UUID.randomUUID(),
                request.userId(),
                request.name(),
                request.order()
        );

        return boardRepository.save(board);
    }
}
