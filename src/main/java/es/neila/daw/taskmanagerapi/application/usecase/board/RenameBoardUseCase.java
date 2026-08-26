package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.RenameBoardRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import org.springframework.stereotype.Component;


public class RenameBoardUseCase {

    private final BoardRepository boardRepository;
    private final DomainEventPublisher eventPublisher;

    public RenameBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher) {
        this.boardRepository = boardRepository;
        this.eventPublisher = eventPublisher;
    }

    public Board execute(RenameBoardRequest request) {
        Board board = boardRepository.findById(request.boardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.rename(request.newName());

        Board savedBoard =  boardRepository.save(board);

        eventPublisher.publish(new AuditDomainEvent(
                savedBoard.getId(),
                "BOARD",
                "RENAMED",
                null,
                "Board update with name: " + savedBoard.getName()
        ));

        return savedBoard;
    }
}
