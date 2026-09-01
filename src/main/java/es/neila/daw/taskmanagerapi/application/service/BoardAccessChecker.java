package es.neila.daw.taskmanagerapi.application.service;

import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;

import java.util.UUID;

public class BoardAccessChecker {

    private final BoardMemberRepository boardMemberRepository;

    public BoardAccessChecker(BoardMemberRepository boardMemberRepository) {
        this.boardMemberRepository = boardMemberRepository;
    }

    public void verifyCanEditContent(UUID boardId, UUID userId) {
        boardMemberRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("You don't have access to this board"));
    }
}
