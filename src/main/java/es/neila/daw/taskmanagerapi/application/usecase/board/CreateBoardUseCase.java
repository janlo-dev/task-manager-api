package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.CreateBoardRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.UUID;

public class CreateBoardUseCase {

    private final BoardRepository boardRepository;
    private final DomainEventPublisher eventPublisher;

    public CreateBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        this.boardRepository = boardRepository;
        this.eventPublisher = eventPublisher;
    }

    public Board execute(CreateBoardRequest request) {
        Board board = new Board(
                UUID.randomUUID(),
                request.userId(),
                request.name(),
                request.boardOrder()
        );

        Board savedBoard =  boardRepository.save(board);

        eventPublisher.publish(new AuditDomainEvent(
                savedBoard.getId(),
                "Board",
                "CREATED",
                request.userId(),
                "Board created with name: " + savedBoard.getName()
        ));

        return savedBoard;
    }
}
