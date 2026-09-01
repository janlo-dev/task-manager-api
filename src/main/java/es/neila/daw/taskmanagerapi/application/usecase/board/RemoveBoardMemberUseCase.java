package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.UUID;

public class RemoveBoardMemberUseCase {

    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final DomainEventPublisher eventPublisher;

    public RemoveBoardMemberUseCase(BoardRepository boardRepository, BoardMemberRepository boardMemberRepository, DomainEventPublisher eventPublisher) {
        this.boardRepository = boardRepository;
        this.boardMemberRepository = boardMemberRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(UUID boardId, UUID memberUserId, UUID performedBy) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.verifyCanManage(performedBy);   // solo el OWNER puede expulsar

        if (memberUserId.equals(performedBy)) {
            throw new IllegalArgumentException("The owner cannot remove themselves from the board");
        }

        boardMemberRepository.deleteByBoardIdAndUserId(boardId, memberUserId);

        eventPublisher.publish(new AuditDomainEvent(
                boardId,
                "BOARD",
                "MEMBER_REMOVED",
                performedBy,
                "User removed from board: " + memberUserId
        ));
    }
}
