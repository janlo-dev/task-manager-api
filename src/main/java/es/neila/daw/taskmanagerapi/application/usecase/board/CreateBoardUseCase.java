package es.neila.daw.taskmanagerapi.application.usecase.board;

import es.neila.daw.taskmanagerapi.application.dto.CreateBoardRequest;
import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.BoardMember;
import es.neila.daw.taskmanagerapi.domain.model.BoardRole;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardMemberRepository;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;

import java.util.UUID;

public class CreateBoardUseCase {

    private final BoardRepository boardRepository;
    private final DomainEventPublisher eventPublisher;
    private final BoardMemberRepository boardMemberRepository;

    public CreateBoardUseCase(BoardRepository boardRepository, DomainEventPublisher eventPublisher, BoardMemberRepository boardMemberRepository) {
        this.boardRepository = boardRepository;
        this.eventPublisher = eventPublisher;
        this.boardMemberRepository = boardMemberRepository;
    }

    public Board execute(CreateBoardRequest request,  UUID userId) {
        Board board = new Board(
                UUID.randomUUID(),
                userId,
                request.name(),
                request.boardOrder()
        );

        Board savedBoard =  boardRepository.save(board);

        BoardMember owner = new BoardMember(
                UUID.randomUUID(),
                savedBoard.getId(),
                userId,
                BoardRole.OWNER
        );
        boardMemberRepository.save(owner);

        eventPublisher.publish(new AuditDomainEvent(
                savedBoard.getId(),
                "Board",
                "CREATED",
                userId,
                "Board created with name: " + savedBoard.getName()
        ));

        return savedBoard;
    }
}
