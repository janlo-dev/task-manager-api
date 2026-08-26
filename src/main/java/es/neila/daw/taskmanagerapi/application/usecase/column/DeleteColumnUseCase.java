package es.neila.daw.taskmanagerapi.application.usecase.column;

import es.neila.daw.taskmanagerapi.domain.event.AuditDomainEvent;
import es.neila.daw.taskmanagerapi.domain.model.Board;
import es.neila.daw.taskmanagerapi.domain.model.Column;
import es.neila.daw.taskmanagerapi.domain.port.DomainEventPublisher;
import es.neila.daw.taskmanagerapi.domain.repository.BoardRepository;
import es.neila.daw.taskmanagerapi.domain.repository.ColumnRepository;

import java.util.UUID;

public class DeleteColumnUseCase {

    private final ColumnRepository columnRepository;
    private final DomainEventPublisher eventPublisher;
    private final BoardRepository boardRepository;

    public DeleteColumnUseCase(ColumnRepository columnRepository, DomainEventPublisher eventPublisher, BoardRepository boardRepository) {
        this.columnRepository = columnRepository;
        this.eventPublisher = eventPublisher;
        this.boardRepository = boardRepository;
    }

    public void execute(UUID columnId, UUID performedByUserId) {

        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new IllegalArgumentException("Column not found"));

        Board board = boardRepository.findById(column.getBoardId())
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.verifyCanEditContent(performedByUserId);

        columnRepository.delete(columnId);

        eventPublisher.publish(new AuditDomainEvent(
                columnId,
                "COLUMN",
                "DELETED",
                performedByUserId,
                "Column deleted: " + column.getName()
        ));
    }
}
