package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.ChangeBoardOrderRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.UUID;


public class ChangeBoardOrderUseCase {

    private final BoardRepository boardRepository;
    private final DomainEventPublisher eventPublisher;

    public ChangeBoardOrderUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        this.boardRepository = boardRepository;
        this.eventPublisher = eventPublisher;
    }

    public Board execute(ChangeBoardOrderRequest request, UUID performedByUserId) {
        Board board = boardRepository.findById(request.boardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.verifyCanManage(performedByUserId);
        board.changeOrder(request.newOrder());

        Board updateBoard = boardRepository.save(board);

        eventPublisher.publish(new AuditDomainEvent(
                updateBoard.getId(),
                "BOARD",
                "REORDERED",
                performedByUserId,
                "Board order changed to: " + updateBoard.getBoardOrder()
        ));

        return updateBoard;
    }
}
