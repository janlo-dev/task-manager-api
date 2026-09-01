package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;

import java.util.List;
import java.util.UUID;

public class GetBoardMembersUseCase {

    private final BoardMemberRepository boardMemberRepository;

    public GetBoardMembersUseCase(BoardMemberRepository boardMemberRepository) {
        this.boardMemberRepository = boardMemberRepository;
    }

    public List<BoardMember> execute(UUID boardId) {
        return boardMemberRepository.findByBoardId(boardId);
    }
}
