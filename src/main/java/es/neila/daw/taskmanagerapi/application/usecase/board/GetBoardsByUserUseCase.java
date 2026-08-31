package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.List;
import java.util.UUID;

public class GetBoardsByUserUseCase {

    private final BoardRepository boardRepository;

    public GetBoardsByUserUseCase(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public List<Board> execute(UUID userId) {
        return boardRepository.findByUserId(userId);
    }
}
