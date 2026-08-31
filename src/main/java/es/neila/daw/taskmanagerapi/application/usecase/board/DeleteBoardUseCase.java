package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.event.BoardDeletedEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.UUID;

public class DeleteBoardUseCase {

    private final BoardRepository boardRepository;
    private final DomainEventPublisher eventPublisher;

    public DeleteBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        this.boardRepository = boardRepository;
        this.eventPublisher = eventPublisher;
    }

    public void execute(UUID boardId, UUID performedByUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.verifyCanManage(performedByUserId);

        boardRepository.delete(boardId);

        eventPublisher.publish(new BoardDeletedEvent(boardId, performedByUserId));

        eventPublisher.publish(new AuditDomainEvent(
                boardId,
                "BOARD",
                "DELETED",
                performedByUserId,
                "Board deleted: " + board.getName()
        ));
    }
}
