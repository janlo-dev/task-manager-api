package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.List;
import java.util.UUID;

public class GetBoardsByUserUseCase {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;

    public GetBoardsByUserUseCase(BoardRepository boardRepository, BoardMemberRepository boardMemberRepository) {
        this.boardRepository = boardRepository;
        this.boardMemberRepository = boardMemberRepository;
    }

    public List<Board> execute(UUID userId) {

        List<BoardMember> memberships = boardMemberRepository.findByUserId(userId);

        return memberships.stream()
                .map(BoardMember::getBoardId)
                .map(boardId -> boardRepository.findById(boardId)
                        .orElseThrow(() -> new IllegalArgumentException("Board not found")))
                .toList();
    }
}
