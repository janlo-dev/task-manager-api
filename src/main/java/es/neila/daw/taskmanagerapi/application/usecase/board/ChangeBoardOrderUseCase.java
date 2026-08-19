package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.ChangeBoardOrderRequest;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import org.springframework.stereotype.Component;


public class ChangeBoardOrderUseCase {

    private final BoardRepository boardRepository;

    public ChangeBoardOrderUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board execute(ChangeBoardOrderRequest request) {
        Board board = boardRepository.findById(request.boardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.changeOrder(request.newOrder());

        return boardRepository.save(board);
    }
}
