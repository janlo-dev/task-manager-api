package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.RenameBoardRequest;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import org.springframework.stereotype.Component;


public class RenameBoardUseCase {

    private final BoardRepository boardRepository;

    public RenameBoardUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Board execute(RenameBoardRequest request) {
        Board board = boardRepository.findById(request.boardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.rename(request.newName());

        return boardRepository.save(board);
    }
}
